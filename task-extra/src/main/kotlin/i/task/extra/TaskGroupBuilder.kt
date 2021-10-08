package i.task.extra

import i.task.ITask
import i.task.ITaskHook
import i.task.ITaskManager
import i.task.ITaskStatus
import i.task.ITaskSubmitOption
import i.task.ITaskSubmitOptions
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass

class TaskGroupBuilder<OPT : ITaskSubmitOptions, RES : Any>(
    private val taskManager: ITaskManager<OPT>,
    private val name: String
) {
    class TaskCallback<RES : Any>(
        override var success: (RES) -> Unit = {},
        override var fail: (Throwable) -> Unit = {}
    ) : ITaskHook<RES>

    private val options: MutableMap<KClass<out ITaskSubmitOption<*>>, Any> = mutableMapOf()
    private val callBack = TaskCallback<RES>()
    private val tasks = ConcurrentLinkedDeque<ITask<out Any>>()
    fun append(vararg task: ITask<out Any>) = kotlin.run {
        tasks.addAll(task)
        this
    }

    fun <T : Any> putOption(clazz: KClass<out ITaskSubmitOption<T>>, data: T) = kotlin.run {
        options[clazz] = data
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
