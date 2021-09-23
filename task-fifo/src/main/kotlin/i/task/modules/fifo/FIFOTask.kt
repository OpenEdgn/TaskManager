package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskConfiguration
import i.task.ITaskContext
import i.task.ITaskGroupConfiguration

/**
 * 任务
 */
class FIFOTask(
    override val task: ITask<*>,
    private val taskGroup: ITaskGroupConfiguration
) : IFIFOTask, ITaskContext, ITaskConfiguration {
    @Volatile
    override var process: Float = 0f
    val key: String
        get() = task.key

    override val currentGroup: ITaskGroupConfiguration
        get() = taskGroup

    override val currentTask: ITaskConfiguration
        get() = this

    override fun updateProcess(process: Float) {
        if (process > 1 || process < 0) {
            return
        }
        this.process = process
    }
}
