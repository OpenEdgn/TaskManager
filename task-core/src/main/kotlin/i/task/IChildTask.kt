package i.task

/**
 * 子任务
 */
interface IChildTask<T : Any> {
    /**
     * 任务名称
     */
    val name: String

    /**
     * 声明独占的资源ID
     *
     * 表明任务需独占的资源代号，保证多个任务不会发生资源竞争
     *
     * 注意，此对象生命周期应与 IChildTask 一致且不应在运行时发生变动
     */
    val lock: Set<String>

    /**
     * 任务前置测试
     *
     * 在此子任务执行之前将调用此函数做任务可行性分析，
     * 如果不满足导致任务失败并执行回滚，抛出异常同理
     */
    fun check(context: IChildTaskContext): Boolean

    /**
     * 任务执行函数
     *
     *  任务在此函数下执行任务，可返回值让下一个任务或者回调获取
     *  如果任务发生错误将导致任务组异常退出，并触发回滚，
     *  如需清除创建的中间内容可在 `close` 方法下处理
     *  请勿在此函数下执行任何不可恢复的操作！
     */
    fun run(context: IChildTaskContext): T

    /**
     * 任务结果提交
     *
     * 当一组任务执行完成后，触发提交操作，
     * 如果提交过程中出现问题将触发回滚，
     * 请勿在此函数下执行任何不可恢复的操作！
     */
    fun submit(context: IChildTaskContext, data: T)

    /**
     * 任务回滚函数
     *
     * 当一组任务发生错误时，将回调此函数进行回滚，此函数抛出的错误不会影响后续任务执行
     */
    fun rollback(info: IRollbackInfo)

    /**
     * 任务销毁函数
     *
     * 当任务成功或回滚后将回调此函数，此函数产生的错误不会影响后续任务执行
     *
     * @param finish 此任务组是否执行完成且未发生错误
     */
    fun close(finish: Boolean)
}
