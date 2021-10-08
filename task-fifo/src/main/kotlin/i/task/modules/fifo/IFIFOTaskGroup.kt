package i.task.modules.fifo

import i.task.ITask
import i.task.TaskRollbackInfo
import java.util.Optional

/**
 * 一组任务
 */
interface IFIFOTaskGroup<RES : Any> : Runnable {

    val name: String

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
    fun error(info: TaskRollbackInfo)

    /**
     * 要求子任务处理回调
     */
    fun hook(runner: ITaskFinishRunner, error: Optional<Throwable>)

    /**
     * 提交任务结果
     */
    fun comment()

    fun close(finish: Boolean)
}
