package i.task.modules.fifo

import i.task.ITask
import i.task.TaskRollbackInfo
import java.io.Closeable
import java.util.Optional
import java.util.concurrent.ExecutorService

/**
 * 一组任务
 */
interface IFIFOTaskGroup<RES : Any> : Runnable, Closeable {

    val name: String

    /**
     * 任务状态信息
     */
    val taskStatusCall: FIFOTaskStatus<RES>

    /**
     * 任务组包含的任务
     */
    val tasks: List<ITask<*>>

    /**
     * 任务进度
     */
    val process: Float

    /**
     * 任务数量
     */
    val size: Int

    /**
     * 尝试停止任务组
     *
     */
    fun cancel(info: TaskRollbackInfo)

    /**
     * 要求子任务处理回调
     */
    fun callBack(threadPool: ExecutorService, error: Optional<Throwable>)
}
