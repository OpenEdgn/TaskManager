package i.task.extra

import i.task.ITaskGroupOptions
import i.task.ITaskManager

fun <CFG : TaskManagerFeature.Configuration, OPT : ITaskGroupOptions> taskManager(
    feature: TaskManagerFeature<OPT, out ITaskManager<OPT>, CFG>,
    config: CFG.() -> Unit = {}
) = TaskManager(feature, config)
