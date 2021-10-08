package i.task

import kotlin.reflect.KClass

/**
 * 任务调度管理器
 */
interface ITaskManager<Option : ITaskSubmitOptions> {
    /**
     * 任务管理器名称
     */
    val name: String

    /**
     * 获取任务信息
     *
     */
    val taskInfo: ITaskManagerInfo

    /**
     * 提交一组串行任务
     *
     * 注意：如果执行任务发生错误时，后续任务将全部取消并触发回滚
     *
     * @param task List<ITask<*>> 一组任务
     * @param call ITaskCallBack<RES> 任务回调
     * @param options 指定这组任务的配置信息
     * @return ITaskStatus<RES> 返回任务状态
     */
    fun <RES : Any> submit(
        name: String,
        task: List<ITask<*>>,
        options: Map<KClass<out ITaskSubmitOption<*>>, Any>,
        call: ITaskHook<RES>
    ): ITaskStatus<RES>

    /**
     * 提交任务时可选选项
     */
    val options: Option

    /**
     * 不再接收新任务，等待执行完成后退出
     */

    fun shutdown()
}
