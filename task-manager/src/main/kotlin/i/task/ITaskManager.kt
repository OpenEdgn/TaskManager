package i.task

/**
 * 任务调度管理器
 */
interface ITaskManager {
    /**
     * 获取任务信息
     *
     */
    val taskInfo: ITaskInfo

    /**
     * 提交一组串行任务
     *
     * 注意：如果执行任务发生错误时，后续任务将全部取消
     *
     * @param task Array<out ITask<Any>> 一组任务
     * @param call Function1<T, Unit> 任务回调
     * @return String 返回任务 ID，可根据任务ID 取消对应任务
     */
    fun <T : Any, RES : Any> submit(
        vararg task: ITask<Any>,
        call: TaskCallBack<T, RES>
    ): ITaskStatus<T, RES>

    /**
     * 不再接收新任务，等待执行完成后退出
     */
    fun shutdown()
}
