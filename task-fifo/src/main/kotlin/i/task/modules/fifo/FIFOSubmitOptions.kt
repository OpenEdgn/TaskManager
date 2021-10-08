package i.task.modules.fifo

import i.task.ITaskSubmitOption
import i.task.ITaskSubmitOptions
import i.task.options.WaitFinishTaskOption
import kotlin.reflect.KClass

object FIFOSubmitOptions : ITaskSubmitOptions {
    val WAIT_FINISH = WaitFinishTaskOption::class
    override val listOptions: List<ITaskSubmitOption<*>> = listOf(
        WaitFinishTaskOption(true),
    )

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getConfig(
        data: Map<KClass<out ITaskSubmitOption<*>>, Any>,
        clazz: KClass<out ITaskSubmitOption<T>>
    ): T {
        return try {
            data[clazz] ?: throw NullPointerException()
        } catch (e: Exception) {
            listOptions.first { it::class == clazz }.value
        } as T
    }
}
