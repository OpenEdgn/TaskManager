package i.task.extra

import i.task.ITaskManager
import i.task.ITaskSubmitOptions

fun <CFG : TaskManagerFeature.Configuration, OPT : ITaskSubmitOptions> taskManager(
    feature: TaskManagerFeature<OPT, out ITaskManager<OPT>, CFG>,
    config: CFG.() -> Unit = {}
) = TaskManager(feature, config)
