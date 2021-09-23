package i.task.extra

import i.task.ITask
import i.task.ITaskCallBack
import i.task.ITaskInfo
import i.task.ITaskManager
import i.task.ITaskOption
import i.task.ITaskOptions
import i.task.ITaskStatus

/**
 * 抽象化任务管理器
 *
 */
class TaskManager<CFG : TaskManagerFeature.Configuration, OPT : ITaskOptions>(
    feature: TaskManagerFeature<OPT, out ITaskManager<OPT>, CFG>,
    config: CFG.() -> Unit = {}
) : ITaskManager<OPT> {
    private val taskManagerImpl = kotlin.run {
        val cfg = feature.config
        config(cfg)
        feature.newTaskManager(cfg)
    }
    override val name: String
        get() = taskManagerImpl.name
    override val taskInfo: ITaskInfo
        get() = taskManagerImpl.taskInfo

    override fun <RES : Any> submit(
        name: String,
        task: List<ITask<*>>,
        options: List<ITaskOption<*>>,
        call: ITaskCallBack<RES>
    ): ITaskStatus<RES> {
        return taskManagerImpl.submit(name, task, options, call)
    }

    override val options: OPT
        get() = taskManagerImpl.options

    fun <T : Any> submit(task: ITask<T>, callBack: ITaskCallBack<T>): ITaskStatus<T> {
        return submit(task.key, listOf(task), emptyList(), callBack)
    }

    fun <RES : Any> submit(name: String) = TaskGroupBuilder<OPT, RES>(this, name)

    override fun shutdown() {
        taskManagerImpl.shutdown()
    }
}
