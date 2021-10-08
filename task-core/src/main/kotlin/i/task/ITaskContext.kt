package i.task

/**
 * 任务管理器上下文
 *
 */
interface ITaskContext {
    /**
     *  管理当前任务组信息
     */
    val currentGroup: ITaskGroupOption

    /**
     * 管理当前任务信息
     */
    val currentTask: ITaskOption
}
