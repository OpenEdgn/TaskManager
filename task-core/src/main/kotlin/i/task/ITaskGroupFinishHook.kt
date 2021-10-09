package i.task

/**
 * 任务回调
 *
 * 一个或一组任务结束后，只存在2种状态，即成功或失败
 *
 * @param RES : Any 任务返回结果
 * @property success Function1<RES, Unit> 成功回调函数
 * @property fail Function1<Throwable, Unit> 失败回调函数
 */
interface ITaskGroupFinishHook<RES : Any> {
    val success: (RES) -> Unit
    val fail: (Throwable) -> Unit
}
