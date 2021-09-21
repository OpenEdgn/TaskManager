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
     * 获取任务信息
     *
     */
    val taskInfo: ITaskInfo

    /**
     * 提交一组串行任务
     *
     * 注意：如果执行任务发生错误时，后续任务将全部取消并触发回滚
     *
     * @param task Array<out ITask<Any>> 一组任务
     * @param call Function1<T, Unit> 任务回调
     * @return String 返回任务 ID，可根据任务ID 取消对应任务
     */
    fun <RES : Any> submit(
        name: String,
        task: List<ITask<*>>,
        call: TaskCallBack<RES>
    ): ITaskStatus<RES>

    /**
     * 不再接收新任务，等待执行完成后退出
     */
    fun shutdown()
}
