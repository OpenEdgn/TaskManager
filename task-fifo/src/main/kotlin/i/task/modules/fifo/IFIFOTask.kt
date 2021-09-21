package i.task.modules.fifo

import i.task.ITask

interface IFIFOTask {
    val task: ITask<*>
    val process: Float
}
