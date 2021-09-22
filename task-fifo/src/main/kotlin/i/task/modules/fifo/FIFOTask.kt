package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskContext
import i.task.ITaskGroupOptions
import i.task.ITaskOptions

/**
 * 任务
 */
class FIFOTask(
    override val task: ITask<*>,
    private val taskGroup: ITaskGroupOptions
) : IFIFOTask, ITaskContext, ITaskOptions {
    @Volatile
    override var process: Float = 0f
    val key: String
        get() = task.key

    override val currentGroup: ITaskGroupOptions
        get() = taskGroup

    override val currentTask: ITaskOptions
        get() = this

    override fun updateProcess(process: Float) {
        if (process > 1 || process < 0) {
            return
        }
        this.process = process
    }
}
