package i.task

abstract class ITaskOption<TYPE : Any>(val value: TYPE) {
    abstract val key: String
}
