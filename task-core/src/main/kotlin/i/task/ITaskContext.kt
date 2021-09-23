package i.task

/**
 * 任务上下文
 */
interface ITaskContext {
    /**
     *  管理当前任务组信息
     */
    val currentGroup: ITaskGroupConfiguration

    /**
     * 管理当前任务信息
     */
    val currentTask: ITaskConfiguration
}
