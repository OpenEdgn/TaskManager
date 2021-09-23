package i.task

/**
 * 任务回调执行容器
 */
interface ITaskFinishRunner {
    /**
     * 提交回调任务
     */
    fun submit(runnable: Runnable)

    /**
     * 任务管理器停止事件
     */
    fun shutdown()
}
