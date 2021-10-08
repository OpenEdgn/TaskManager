package i.task

/**
 * 任务提交设置
 */
interface ITaskSubmitOption<TYPE : Any> {
    /**
     * 任务值
     */
    val value: TYPE
}
