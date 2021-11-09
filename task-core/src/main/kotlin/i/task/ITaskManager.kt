package i.task

/**
 * 任务调度管理器
 */
interface ITaskManager {
    /**
     * 任务管理器名称
     */
    val name: String

    /**
     * 提交一组串行任务
     *
     * 注意：如果执行任务发生错误时，后续任务将全部取消并触发回滚
     *
     * @param task 提交的任务
     * @return ITaskStatus<RES> 返回任务状态
     */
    fun <RES : Any> submit(
        task: ITask<RES>
    ): ITaskResult<RES>

    /**
     * 不再接收新任务，等待执行完成后退出
     */
    fun shutdown()
}
