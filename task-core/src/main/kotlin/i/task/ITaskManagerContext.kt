package i.task

/**
 * 任务管理器上下文
 *
 */
interface ITaskManagerContext {
    /**
     *  管理当前任务组信息
     */
    val currentGroup: ITaskGroupContext

    /**
     * 管理当前任务信息
     */
    val currentTask: ITaskContext
}
