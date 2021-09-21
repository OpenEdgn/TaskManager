package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskGroupOptions
import i.task.TaskCallBack
import i.task.TaskException.CheckFailException
import i.task.TaskRollbackInfo
import i.task.TaskRollbackInfo.RollbackType
import i.task.TaskStatus
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * 任务组
 */
class FIFOTaskGroup<RES : Any>(
    override val name: String,
    override val tasks: List<ITask<*>>,
    private val call: TaskCallBack<RES>
) : IFIFOTaskGroup<RES>, ITaskGroupOptions {
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

    private var lastTaskResult: Optional<Any> = Optional.empty()

    @Volatile
    private var realTask: FIFOTask = tasksWrapper.first()

    override fun run() {
        synchronized(tasksWrapper) {
            logger.debug(marker, "开始执行此任务组.")
            status = TaskStatus.RUNNING
            for (value in tasksWrapper) {
                if (fail.get()) {
                    logger.debug(marker, "任务组监测到停止信号，中止任务组.")
                    break
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
                    logger.debug(marker, "任务 \"{}\" 前置校验失败.", value.key)
                    throw CheckFailException(value.key)
                }
                value.process = 1f
                logger.debug(marker, "任务 \"{}\" 结束.", value.key)
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
            for (value in (0..last).reversed()) {
                try {
                    val fifoTask = tasksWrapper[value]
                    if ((last - 1) == value) {
                        fifoTask.task.rollback(info)
                    } else {
                        fifoTask.task.rollback(anotherInfo)
                    }
                } catch (e: Throwable) {
                }
            }
        }
    }

    override fun callBack(threadPool: ExecutorService, error: Optional<Throwable>) {
        if (fail.get()) {
            threadPool.submit {
                call.fail(error.get())
            }
        } else {
            threadPool.submit {
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
        logger.debug(marker, "开始销毁任务.")
        for (value in (0 until tasksWrapper.size).reversed()) {
            try {
                tasksWrapper[value].task.close()
                // 清除缓存
            } catch (e: Throwable) {
            }
        }
        status = TaskStatus.FINISH
        tasksWrapper.clear()
        properties.clear()
        logger.debug(marker, "任务组已销毁.")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(FIFOTaskGroup::class.java)
    }
}
