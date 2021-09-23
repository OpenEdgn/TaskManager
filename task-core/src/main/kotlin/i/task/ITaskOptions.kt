package i.task

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

interface ITaskOptions {
    val listOptions: List<ITaskOption<*>>

    fun <T : Any> newOption(clazz: KClass<out ITaskOption<T>>, value: T): ITaskOption<T> {
        return clazz.primaryConstructor!!.call(value)
    }
}
