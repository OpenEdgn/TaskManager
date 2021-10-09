package i.task

/**
 * 任务提交时可配置选项
 */
interface ITaskGroupOptions {
    /**
     * 此任务管理器所支持的全部选项
     */
    val listOptions: List<ITaskGroupOption<*>>
}
