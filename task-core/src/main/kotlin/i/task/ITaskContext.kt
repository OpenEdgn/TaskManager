package i.task

import java.util.Optional

/**
 * 任务组上下文
 */
interface ITaskContext {
    /**
     * 任务组共享数据
     */
    val shareData: MutableMap<String, Any>

    /**
     * 获取当前任务组上次任务
     *
     * 调用此方法需格外小心，如果类型不一致将导致任务执行失败,可能会出现类型不匹配错误！
     */
    fun <T : Any> lastResult(): Optional<T>
}
