package i.task.options

import i.task.ITaskSubmitOption

/**
 * 阻塞至任务结束后执行
 */
class WaitFinishTaskOption(override val value: Boolean) : ITaskSubmitOption<Boolean>
