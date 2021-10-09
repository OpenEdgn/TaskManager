package i.task.extra

import i.task.ITask
import i.task.ITaskGroupFinishHook
import i.task.ITaskGroupOption
import i.task.ITaskGroupOptions
import i.task.ITaskGroupResult
import i.task.ITaskManager
import i.task.ITaskManagerInfo
import kotlin.reflect.KClass

/**
 * 抽象化任务管理器
 *
 */
class TaskManager<CFG : TaskManagerFeature.Configuration, OPT : ITaskGroupOptions>(
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
        options: Map<KClass<out ITaskGroupOption<*>>, Any>,
        call: ITaskGroupFinishHook<RES>
    ): ITaskGroupResult<RES> {
        return taskManagerImpl.submit(name, task, options, call)
    }

    override val options: OPT
        get() = taskManagerImpl.options

    fun <T : Any> submit(task: ITask<T>, callBack: ITaskGroupFinishHook<T>): ITaskGroupResult<T> {
        return submit(task.name, listOf(task), emptyMap(), callBack)
    }

    fun <RES : Any> submit(name: String) = TaskGroupBuilder<OPT, RES>(this, name)

    override fun shutdown() {
        taskManagerImpl.shutdown()
    }

    override fun shutdownNow() {
        taskManagerImpl.shutdownNow()
    }
}
