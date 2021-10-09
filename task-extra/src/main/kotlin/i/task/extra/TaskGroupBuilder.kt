package i.task.extra

import i.task.ITask
import i.task.ITaskGroupFinishHook
import i.task.ITaskGroupOption
import i.task.ITaskGroupOptions
import i.task.ITaskGroupResult
import i.task.ITaskManager
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass

class TaskGroupBuilder<OPT : ITaskGroupOptions, RES : Any>(
    private val taskManager: ITaskManager<OPT>,
    private val name: String
) {
    class TaskGroupFinishCallback<RES : Any>(
        override var success: (RES) -> Unit = {},
        override var fail: (Throwable) -> Unit = {}
    ) : ITaskGroupFinishHook<RES>

    private val options: MutableMap<KClass<out ITaskGroupOption<*>>, Any> = mutableMapOf()
    private val callBack = TaskGroupFinishCallback<RES>()
    private val tasks = ConcurrentLinkedDeque<ITask<out Any>>()
    fun append(vararg task: ITask<out Any>) = kotlin.run {
        tasks.addAll(task)
        this
    }

    fun <T : Any> putOption(clazz: KClass<out ITaskGroupOption<T>>, data: T) = kotlin.run {
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

    fun submit(): ITaskGroupResult<RES> {
        val submit = taskManager.submit(name, tasks.toList(), options, callBack)
        tasks.clear()
        return submit
    }
}
