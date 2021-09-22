package i.task.modules.fifo

import i.task.ITaskStatus
import i.task.TaskStatus
import java.util.Optional

class FIFOTaskStatus<RES : Any>(private val fifoTaskGroup: FIFOTaskGroup<RES>) : ITaskStatus<RES> {
    override val status: TaskStatus
        get() = fifoTaskGroup.status
    override val value: Optional<RES>
        get() = fifoTaskGroup.lastTaskResult()
    override val process: Float
        get() = fifoTaskGroup.process

    override fun cancel() {
        fifoTaskGroup.tryExit()
    }
}
