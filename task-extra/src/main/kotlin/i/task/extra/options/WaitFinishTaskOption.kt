package i.task.extra.options

import i.task.ITaskGroupOption

/**
 * 阻塞至任务结束后执行
 */
class WaitFinishTaskOption(override val value: Boolean) : ITaskGroupOption<Boolean>
