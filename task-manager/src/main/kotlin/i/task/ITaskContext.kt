package i.task

/**
 * 任务上下文
 */
interface ITaskContext {

    /**
     * 管理当前任务信息
     */
    fun currentTask(): ITaskOptions
}
