package i.task.extra

import i.task.ITask
import i.task.ITaskCallBack
import i.task.ITaskManager
import i.task.ITaskOption
import i.task.ITaskOptions
import i.task.ITaskStatus
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass

class TaskGroupBuilder<OPT : ITaskOptions, RES : Any>(
    private val taskManager: ITaskManager<OPT>,
    private val name: String
) {
    class TaskCallback<RES : Any>(
        override var success: (RES) -> Unit = {},
        override var fail: (Throwable) -> Unit = {}
    ) : ITaskCallBack<RES>

    private val options: MutableList<ITaskOption<*>> = mutableListOf()
    private val callBack = TaskCallback<RES>()
    private val tasks = ConcurrentLinkedDeque<ITask<out Any>>()
    fun append(vararg task: ITask<out Any>) = kotlin.run {
        tasks.addAll(task)
        this
    }

    fun putOption(vararg option: ITaskOption<*>) = kotlin.run {
        options.addAll(option)
        this
    }

    fun <T : Any> putOption(clazz: KClass<out ITaskOption<T>>, data: T) = kotlin.run {
        putOption(taskManager.options.newOption(clazz, data))
        this
    }

    fun success(call: (RES) -> Unit) = kotlin.run {
        callBack.success = call
        this
    }

    fun fail(call: (Throwable) -> Unit) = kotlin.run {
        callBack.fail = call
        this
    }

    fun submit(): ITaskStatus<RES> {
        val submit = taskManager.submit(name, tasks.toList(), options, callBack)
        tasks.clear()
        return submit
    }
}
