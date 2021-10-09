package i.task

import kotlin.reflect.KClass

/**
 * 任务调度管理器
 */
interface ITaskManager<Options : ITaskGroupOptions> {
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
        options: Map<KClass<out ITaskGroupOption<*>>, Any>,
        call: ITaskGroupFinishHook<RES>
    ): ITaskGroupResult<RES>

    /**
     * 提交任务时可选选项
     */
    val options: Options

    /**
     * 不再接收新任务，等待执行完成后退出
     */

    fun shutdown()

    /**
     * 立即清除所有未执行的任务，已执行的任务等待其继续执行
     */
    fun shutdownNow()
}
