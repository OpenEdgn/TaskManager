package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskGroupOptions
import i.task.TaskCallBack
import i.task.TaskException.CheckFailException
import i.task.TaskRollbackInfo
import i.task.TaskRollbackInfo.RollbackType
import i.task.TaskStatus
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

            for (value in tasksWrapper) {
                if (fail.get()) {
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
                    throw CheckFailException(value.name)
                }
                value.process = 1f
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
            val error = when (info.type) {
                RollbackType.CURRENT_CHECK_ERROR ->
                    RollbackType.NEXT_CHECK_ERROR

                RollbackType.CURRENT_RUN_ERROR ->
                    RollbackType.NEXT_RUN_ERROR
                else -> info.type
            }
            taskStatusCall.status = TaskStatus.ERROR
            val anotherInfo = TaskRollbackInfo(error, info.error)
            for (value in last..0) {
                try {
                    if (last == value) {
                        tasksWrapper[value].task.rollback(info)
                    } else {
                        tasksWrapper[value].task.rollback(anotherInfo)
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
        for (value in tasksWrapper.size until 0) {
            try {
                tasksWrapper[value].task.close()
                // 清除缓存
            } catch (e: Throwable) {
            }
        }
        taskStatusCall.status = TaskStatus.FINISH
        tasksWrapper.clear()
        properties.clear()
    }
}
