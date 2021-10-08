package i.task

/**
 * 任务管理
 */
interface ITaskOption {

    /**
     * 更新任务进度
     * 最小 0，最大 1f
     */
    fun updateProcess(process: Float)
}
