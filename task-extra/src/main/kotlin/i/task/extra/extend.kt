package i.task.extra

import i.task.ITaskManager
import i.task.ITaskOptions

fun <CFG : TaskManagerFeature.Configuration, OPT : ITaskOptions> taskManager(
    feature: TaskManagerFeature<OPT, out ITaskManager<OPT>, CFG>,
    config: CFG.() -> Unit = {}
) = TaskManager(feature, config)
