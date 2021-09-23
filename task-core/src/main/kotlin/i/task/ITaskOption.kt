package i.task

/**
 * 任务提交设置
 */
abstract class ITaskOption<TYPE : Any>(val value: TYPE) {
    abstract val key: String
}
