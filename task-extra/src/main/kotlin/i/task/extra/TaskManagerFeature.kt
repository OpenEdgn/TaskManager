package i.task.extra

import i.task.ITaskManager

/**
 * 抽象化任务管理器创建者
 *
 * @param MGR : ITaskManager
 * @param CFG : TaskManagerFeature.Configuration
 * @property config CFG
 */
interface TaskManagerFeature<MGR : ITaskManager, CFG : TaskManagerFeature.Configuration> {
    val config: CFG

    fun newTaskManager(config: CFG): MGR

    interface Configuration {
        val name: String
    }
}
