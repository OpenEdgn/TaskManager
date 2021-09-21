package i.task

import java.io.Closeable
import java.io.Serializable

/**
 * 任务实例
 * 注意：任务实例应可以被序列化和反序列化
 */
interface ITask<T : Any> : Closeable, Serializable {

    /**
     * 任务名称，确定任务唯一性的标志
     */

    val key: String

    /**
     * 任务前置测试
     *
     * 可复写此方法来控制任务是否执行，此方法在任务启动前调用
     * 如果返回 false 则表示任务无法执行，将触发回滚
     */
    fun check(context: ITaskContext): Boolean

    /**
     * 任务执行函数
     *
     *  任务执行函数，可返回值让下一个任务或者回调获取
     *  如果任务发生错误将触发回滚
     */
    fun run(context: ITaskContext): T

    /**
     * 任务回滚函数
     *
     * 当一组任务发生错误时，调用此函数进行回滚，此函数产生的错误将被忽略
     */
    fun rollback(info: TaskRollbackInfo)

    /**
     * 任务销毁函数
     *
     * 当任务成功或回滚后将调用此函数，此函数产生的错误将被忽略
     */
    override fun close()
}
