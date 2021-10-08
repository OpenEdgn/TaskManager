package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskGroupOption
import i.task.ITaskHook
import i.task.TaskException
import i.task.TaskException.CheckFailException
import i.task.TaskRollbackInfo
import i.task.TaskRollbackInfo.RollbackType
import i.task.TaskStatus
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * 任务组
 */
class FIFOTaskGroup<RES : Any>(
    override val name: String,
    override val tasks: List<ITask<*>>,
    private val call: ITaskHook<RES>
) : IFIFOTaskGroup<RES>, ITaskGroupOption {
    private val marker: Marker by lazy {
        MarkerFactory.getMarker("任务组：\"$name\"")
    }

    @Volatile
    var status: TaskStatus = TaskStatus.READY

    @Suppress("UNCHECKED_CAST")
    override val taskStatusCall = FIFOTaskStatus(this)

    private val tasksWrapper = ArrayList<FIFOTask>(tasks.map { FIFOTask(it, this) })

    override val properties: MutableMap<String, Any> = ConcurrentHashMap()

    override val size = tasks.size

    private val count = AtomicInteger(0)

    private val fail = AtomicBoolean(false)

    private val userCancel = AtomicBoolean(false)

    private var lastTaskResult: Optional<Any> = Optional.empty()

    @Volatile
    private var realTask: FIFOTask = tasksWrapper.first()

    override fun run() {
        synchronized(tasksWrapper) {
            logger.debug(marker, "开始执行此任务组.")
            status = TaskStatus.RUNNING
            for (value in tasksWrapper) {
                if (fail.get()) {
                    logger.debug(marker, "任务组监测到停止信号，中止任务组,当前任务代号：{}.", count.get())
                    break
                }
                if (userCancel.get()) {
                    throw TaskException.UserExitException(name)
                }
                realTask = value
                count.addAndGet(1)
                if (value.task.check(value)) {
                    // 正常结束
                    val result = value.task.run(value)
                    lastTaskResult = if (result is Unit) {
                        Optional.empty()
                    } else {
                        Optional.of(result)
                    }
                } else {
                    // 触发回滚
                    logger.debug(marker, "任务 \"{}\" 前置校验失败.", value.name)
                    throw CheckFailException(value.name)
                }
                value.process = 1f
                logger.debug(marker, "任务 \"{}\" 结束.", value.name)
            }
        }
        // 执行方法
    }

    override val process: Float
        get() {
            var get = count.get()
            if (get < 0) {
                get = 0
            }
            return (get + realTask.process) / size
        }

    override fun cancel(info: TaskRollbackInfo) {
        fail.set(true)
        synchronized(tasksWrapper) { // 等待执行线程释放锁
            val last = count.get()
            val anotherError = when (info.type) {
                RollbackType.CURRENT_CHECK_ERROR ->
                    RollbackType.OTHER_CHECK_ERROR

                RollbackType.CURRENT_RUN_ERROR ->
                    RollbackType.OTHER_RUN_ERROR
                else -> info.type
            }
            status = TaskStatus.ERROR
            val anotherInfo = TaskRollbackInfo(anotherError, info.error)
            for (value in (0 until last).reversed()) {
                val fifoTask = tasksWrapper[value]
                try {
                    logger.debug(marker, "回滚任务 \"{}\".", fifoTask.name)
                    if ((last - 1) == value) {
                        fifoTask.task.rollback(info)
                    } else {
                        fifoTask.task.rollback(anotherInfo)
                    }
                } catch (e: Throwable) {
                    logger.error(marker, "回滚任务 {} 时发生错误.", fifoTask.name, e)
                }
            }
        }
    }

    override fun comment() {
        for (value in tasksWrapper) {
            if (fail.get()) {
                logger.debug(marker, "任务组监测到停止信号，中止任务组,当前任务代号：{}.", count.get())
                break
            }
            if (userCancel.get()) {
                throw TaskException.UserExitException(name)
            }
            realTask = value
            if (value.task.check(value)) {
                // 正常结束
                val result = value.task.run(value)
                lastTaskResult = if (result is Unit) {
                    Optional.empty()
                } else {
                    Optional.of(result)
                }
            } else {
                // 触发回滚
                logger.debug(marker, "任务 \"{}\" 前置校验失败.", value.name)
                throw CheckFailException(value.name)
            }
            value.process = 1f
            logger.debug(marker, "任务 \"{}\" 结束.", value.name)
        }
    }

    override fun callBack(runner: ITaskFinishRunner, error: Optional<Throwable>) {
        if (fail.get()) {
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
        return lastTaskResult as Optional<T>
    }

    override fun close() {
        // 清除
        synchronized(tasksWrapper) {
            logger.debug(marker, "开始销毁任务.")
            for (value in (0 until tasksWrapper.size).reversed()) {
                try {
                    val task = tasksWrapper[value].task
                    logger.debug(marker, "销毁任务 \"{}\".", task.name)
                    task.close()
                    // 清除缓存
                } catch (e: Throwable) {
                }
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
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FIFOTaskGroup::class.java)
    }
}
