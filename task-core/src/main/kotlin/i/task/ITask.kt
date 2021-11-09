package i.task

/**
 *
 * 任务实例
 *
 *一个任务可包含一些子任务，子任务顺序执行，注意，
 * 由于调度算法问题，不同的子任务可能不在同一线程下执行
 *
 * @param RES : Any 最后一个任务的返回类型
 */
interface ITask<RES : Any> {
    /**
     * 任务名称
     */
    val name: String

    /**
     * 子任务
     */
    val childTasks: List<IChildTask<*>>

    /**
     * 任务回调
     */
    val finishCallback: ITaskFinishListener<RES>
}
