package i.task.modules.fifo

import i.task.extra.TaskManagerFeature

data class FIFOConfiguration(
    override val name: String = "fifo-task-manager"
) : TaskManagerFeature.Configuration
