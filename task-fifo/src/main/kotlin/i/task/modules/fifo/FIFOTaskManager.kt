package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskGroupFinishHook
import i.task.ITaskGroupOption
import i.task.ITaskGroupResult
import i.task.ITaskManager
import i.task.ITaskManagerInfo
import i.task.extra.TaskManagerFeature
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

class FIFOTaskManager private constructor(private val config: FIFOConfiguration) : ITaskManager<FIFOSupportedOptions> {
    override val name: String
        get() = TODO("Not yet implemented")
    override val taskInfo: ITaskManagerInfo
        get() = TODO("Not yet implemented")

    override fun <RES : Any> submit(
        name: String,
        task: List<ITask<*>>,
        options: Map<KClass<out ITaskGroupOption<*>>, Any>,
        call: ITaskGroupFinishHook<RES>
    ): ITaskGroupResult<RES> {
        TODO("Not yet implemented")
    }

    override val options: FIFOSupportedOptions
        get() = TODO("Not yet implemented")

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun shutdownNow() {
        TODO("Not yet implemented")
    }

    companion object Feature : TaskManagerFeature<FIFOSupportedOptions, FIFOTaskManager, FIFOConfiguration> {
        private val index = AtomicLong(0)
        override val config: FIFOConfiguration
            get() = FIFOConfiguration(name = "fifo-task-mgr-${index.addAndGet(1)}")

        override fun newTaskManager(config: FIFOConfiguration) = FIFOTaskManager(config)
    }
}
