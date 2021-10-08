package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskContext
import i.task.ITaskGroupOption
import i.task.ITaskOption

/**
 * 任务
 */
class FIFOTask(
    override val task: ITask<*>,
    private val taskGroup: ITaskGroupOption
) : IFIFOTask, ITaskContext, ITaskOption {
    @Volatile
    override var process: Float = 0f
    val name: String
        get() = task.name

    override val currentGroup: ITaskGroupOption
        get() = taskGroup

    override val currentTask: ITaskOption
        get() = this

    override fun updateProcess(process: Float) {
        if (process > 1 || process < 0) {
            return
        }
        this.process = process
    }
}
