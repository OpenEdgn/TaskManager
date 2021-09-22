package i.task.extra

import i.task.ITaskManager

fun <CFG : TaskManagerFeature.Configuration> taskManager(
    feature: TaskManagerFeature<out ITaskManager, CFG>,
    config: CFG.() -> Unit = {}
) = TaskManager(feature, config)
