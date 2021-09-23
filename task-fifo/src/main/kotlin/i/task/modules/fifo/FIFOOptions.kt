package i.task.modules.fifo

import i.task.ITaskOption
import i.task.ITaskOptions
import i.task.options.WaitFinishOption
import kotlin.reflect.KClass

object FIFOOptions : ITaskOptions {
    val WAIT_FINISH = WaitFinishOption::class
    override val listOptions: List<ITaskOption<*>> = listOf(
        WaitFinishOption(),
    )

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getConfig(data: List<ITaskOption<*>>, clazz: KClass<out ITaskOption<T>>): T {
        return try {
            data.first { it::class == clazz }
        } catch (e: Exception) {
            listOptions.first { it::class == clazz }
        }.value as T
    }
}
