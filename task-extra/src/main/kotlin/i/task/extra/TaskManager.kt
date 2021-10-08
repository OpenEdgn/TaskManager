package i.task.extra

import i.task.ITask
import i.task.ITaskHook
import i.task.ITaskManager
import i.task.ITaskManagerInfo
import i.task.ITaskStatus
import i.task.ITaskSubmitOption
import i.task.ITaskSubmitOptions
import kotlin.reflect.KClass

/**
 * 抽象化任务管理器
 *
 */
class TaskManager<CFG : TaskManagerFeature.Configuration, OPT : ITaskSubmitOptions>(
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
    override val taskInfo: ITaskManagerInfo
        get() = taskManagerImpl.taskInfo

    override fun <RES : Any> submit(
        name: String,
        task: List<ITask<*>>,
        options: Map<KClass<out ITaskSubmitOption<*>>, Any>,
        call: ITaskHook<RES>
    ): ITaskStatus<RES> {
        return taskManagerImpl.submit(name, task, options, call)
    }

    override val options: OPT
        get() = taskManagerImpl.options

    fun <T : Any> submit(task: ITask<T>, callBack: ITaskHook<T>): ITaskStatus<T> {
        return submit(task.name, listOf(task), emptyMap(), callBack)
    }

    fun <RES : Any> submit(name: String) = TaskGroupBuilder<OPT, RES>(this, name)

    override fun shutdown() {
        taskManagerImpl.shutdown()
    }
}
