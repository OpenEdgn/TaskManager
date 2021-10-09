package i.task

/**
 * 任务实例
 */
interface ITask<T : Any> {
    /**
     * 任务名称，无特殊含义
     */
    val name: String

    /**
     * 互斥锁ID
     *
     * 表明任务需锁定的资源代号，保证多个任务组下的任务不会发生资源竞争
     *
     * 注意，id 需在任务提交前初始化完成
     */
    val lock: Set<String>

    /**
     * 任务前置测试
     *
     * 可复写此方法来控制任务是否执行，此方法在任务启动前调用
     * 如果返回 false 则表示任务无法执行，将任务组错误，并触发回滚
     * 请勿在此函数执行任何写入操作
     */
    fun check(context: ITaskManagerContext): Boolean

    /**
     * 任务执行函数
     *
     *  任务在此函数下执行任务，可返回值让下一个任务或者回调获取
     *  如果任务发生错误将导致任务组异常退出，并触发回滚，
     *  如需清除创建的中间内容可在 `close` 方法下处理
     *  请勿在此函数下执行任何不可恢复的操作！
     */
    fun run(context: ITaskManagerContext): T

    /**
     * 任务结果提交
     *
     * 当一组任务执行完成后，触发提交操作，
     * 如果提交过程中出现问题将触发回滚，
     * 请勿在此函数下执行任何不可恢复的操作！
     */
    fun submit(context: ITaskManagerContext, data: T)

    /**
     * 任务回滚函数
     *
     * 当一组任务发生错误时，将回调此函数进行回滚，此函数抛出的错误不会影响后续任务执行
     */
    fun rollback(info: ITaskRollbackInfo)

    /**
     * 任务销毁函数
     *
     * 当任务成功或回滚后将回调此函数，此函数产生的错误不会影响后续任务执行
     *
     * @param finish 此任务组是否执行完成且未发生错误
     */
    fun close(finish: Boolean)
}
