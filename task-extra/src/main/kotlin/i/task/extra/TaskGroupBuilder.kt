package i.task.extra

import i.task.ITask
import i.task.ITaskManager
import i.task.ITaskStatus
import i.task.TaskCallBack
import java.util.concurrent.ConcurrentLinkedDeque

class TaskGroupBuilder<RES : Any>(private val taskManager: ITaskManager, val name: String) {
    private var callBack = TaskCallBack.success<RES> { }
    private val tasks = ConcurrentLinkedDeque<ITask<out Any>>()
    fun append(vararg task: ITask<out Any>): TaskGroupBuilder<RES> {
        tasks.addAll(task)
        return this
    }

    fun success(call: (RES) -> Unit): TaskGroupBuilder<RES> {
        callBack = TaskCallBack.success(call)
        return this
    }

    fun fail(call: (Throwable) -> Unit): TaskGroupBuilder<RES> {
        callBack = TaskCallBack.fail(call)
        return this
    }

    fun join(): TaskGroupBuilder<RES> {
        callBack = TaskCallBack.join()
        return this
    }

    fun submit(): ITaskStatus<RES> {
        val submit = taskManager.submit(name, tasks.toList(), callBack)
        tasks.clear()
        return submit
    }
}
