package i.task

/**
 * 当前任务上下文
 */
interface ITaskContext {

    /**
     * 更新任务进度
     * 最小 0，最大 1f
     */
    fun updateProcess(process: Float)
}
