package i.task

import java.util.Optional

/**
 * 任务组上下文
 */
interface ITaskGroupConfiguration {
    /**
     * 任务组共享数据
     */
    val properties: MutableMap<String, Any>

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getProperty(key: String, defaultValue: T? = null): T {
        return (properties[key] ?: defaultValue ?: throw NullPointerException("key : $key not found.")) as T
    }

    /**
     * 获取当前任务组上次任务
     *
     * 调用此方法需格外小心，可能会出现类型不匹配错误！
     */
    fun <T : Any> lastTaskResult(): Optional<T>
}
