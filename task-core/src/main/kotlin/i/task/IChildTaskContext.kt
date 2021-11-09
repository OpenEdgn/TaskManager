package i.task

/**
 * 当前任务上下文
 */
interface IChildTaskContext {

    /**
     * 当前子任务归属的任务上下文
     */
    val task: ITaskContext
}
