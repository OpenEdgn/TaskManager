package i.task.extra

import i.task.ITask
import i.task.ITaskInfo
import i.task.ITaskManager
import i.task.ITaskStatus
import i.task.TaskCallBack

/**
 * 抽象化任务管理器
 *
 */
class TaskManager<CFG : TaskManagerFeature.Configuration>(
    feature: TaskManagerFeature<out ITaskManager, CFG>,
    config: CFG.() -> Unit = {}
) : ITaskManager {
    private val taskManagerImpl = kotlin.run {
        val cfg = feature.config
        config(cfg)
        feature.newTaskManager(cfg)
    }
    override val name: String
        get() = taskManagerImpl.name
    override val taskInfo: ITaskInfo
        get() = taskManagerImpl.taskInfo

    override fun <RES : Any> submit(name: String, task: List<ITask<*>>, call: TaskCallBack<RES>): ITaskStatus<RES> {
        return taskManagerImpl.submit(name, task, call)
    }

    fun <T : Any> submit(task: ITask<T>, callBack: TaskCallBack<T>): ITaskStatus<T> {
        return submit(task.key, listOf(task), callBack)
    }

    fun <RES : Any> submit(name: String) = TaskGroupBuilder<RES>(this, name)

    override fun shutdown() {
        taskManagerImpl.shutdown()
    }
}
