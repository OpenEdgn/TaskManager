package i.task.modules.fifo

import i.task.ITaskGroupOption
import i.task.ITaskGroupOptions
import i.task.extra.options.WaitFinishTaskOption
import kotlin.reflect.KClass

object FIFOSupportedOptions : ITaskGroupOptions {
    override val listOptions: Map<KClass<out ITaskGroupOption<*>>, ITaskGroupOption<*>> = mapOf(
        WaitFinishTaskOption::class to WaitFinishTaskOption(false)
    )
}
