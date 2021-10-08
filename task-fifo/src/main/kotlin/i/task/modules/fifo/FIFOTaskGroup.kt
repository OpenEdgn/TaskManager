package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskGroupOption
import i.task.ITaskHook
import i.task.TaskException
import i.task.TaskRollbackInfo
import i.task.TaskStatus
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * 任务组 ，此类不允许任何多线程实例
 */
class FIFOTaskGroup<RES : Any>(
    override val name: String,
    override val tasks: List<ITask<Any>>,
    private val call: ITaskHook<RES>
) : IFIFOTaskGroup<RES>, ITaskGroupOption {
    private val marker: Marker by lazy {
        MarkerFactory.getMarker("任务组：\"$name\"")
    }

    @Volatile
    var status: TaskStatus = TaskStatus.READY

    private val tasksWrapper = ArrayList<FIFOTask>(tasks.map { FIFOTask(it, this) })

    override val properties: MutableMap<String, Any> = ConcurrentHashMap() // 任务组上下文关联信息

    override val size = tasks.size // 任务数目

    private val executeCount = AtomicInteger(0) // 任务运行、提交计数器

    private val executeFail = AtomicBoolean(false) // 是否发生执行错误

    private val userCancel = AtomicBoolean(false) // 是否出现用户触发错误

    override fun run() {
        status = TaskStatus.RUNNING
        for ((index, taskWrap) in tasksWrapper.withIndex()) {
            val task = taskWrap.task
            if (userCancel.get()) {
                // 用户手动触发退出
                throw TaskException.UserRunExitException(task)
            }
            executeCount.set(index) // 标明当前执行的任务
            val check = try { // 前置检查
                task.check(taskWrap).not()
            } catch (e: Throwable) {
                throw TaskException.CheckThrowException(task, e)
            }
            if (check) {
                throw TaskException.CheckFailException(task)
                // 检查失败，进入错误处理
            }
            try {
                taskWrap.result.set(Optional.ofNullable(task.run(taskWrap)))
            } catch (e: Throwable) {
                throw TaskException.RunFailException(task, e)
                // 任务执行失败，进入错误处理
            }
        }
    }

    override fun comment() {
        status = TaskStatus.RUNNING
        for ((index, taskWrap) in tasksWrapper.withIndex()) {
            val task = taskWrap.task
            if (userCancel.get()) {
                // 用户手动触发退出
                throw TaskException.UserCommentExitException(task)
            }
            executeCount.set(index) // 标明当前执行的任务
            try {
                task.submit(taskWrap, taskWrap.result.get())
            } catch (e: Throwable) {
                // 提交错误
                throw TaskException.CommentFailException(task, e)
            }
        }
    }

    /**
     * 注意：此方法被执行则表明任务始终错误！ 此方法只允许工作线程调用，不存在线程安全问题
     */
    override fun error(info: TaskRollbackInfo) {
        if (status != TaskStatus.FINISH) {
            status = TaskStatus.FINISH
        } else {
            return
        }
        userCancel.set(true) // 标记触发退出
        executeFail.set(true) // 标记已错误
        // 开始回滚
        val last = when {
            info.isUserRunCancel || info.isCheckError -> executeCount.get() // 校验错误，回滚之前的任务
            info.isUserCommentCancel || info.isRunError -> executeCount.get() + 1 // 运行错误，回滚之前的任务
            info.isCommentError -> size - 1 // 提交错误，全量回滚
            else -> 0
        }
        for (value in (0 until last).reversed()) {
            val task = tasksWrapper[value].task
            try {
                task.rollback(info)
            } catch (e: Throwable) {
                logger.warn(marker, "回滚任务 {} 发生错误！", task.name, e)
            }
        }
    }

    override val process: Float = TODO()

    override fun hook(runner: ITaskFinishRunner, error: Optional<Throwable>) {
        if (executeFail.get()) {
            runner.submit {
                call.fail(error.get())
            }
        } else {
            runner.submit {
                call.success(lastTaskResult<RES>().get())
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> lastTaskResult(): Optional<T> {
        val count = executeCount.get()
        return if (count == 0) {
            Optional.empty()
        } else {
            tasksWrapper[count - 1].result.get() as Optional<T>
        }
    }

    override fun close(finish: Boolean) {
        // 清除
        logger.debug(marker, "开始销毁任务.")
        for (value in (0 until tasksWrapper.size).reversed()) {
            try {
                val task = tasksWrapper[value].task
                logger.debug(marker, "销毁任务 '{}'.", task.name)
                task.close(finish)
                // 清除缓存
            } catch (e: Throwable) {
            }
        }

        status = TaskStatus.FINISH
        tasksWrapper.clear()
        properties.clear()
        logger.debug(marker, "任务组已销毁.")
    }

    fun tryExit() {
        if (status == TaskStatus.FINISH) {
            return
        }
        logger.debug(marker, "发送任务组手动终止信号.")
        userCancel.set(true)
        TODO("需处理任务未开始时销毁")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FIFOTaskGroup::class.java)
    }
}
