package i.task

/**
 * 异常抛出
 */
open class TaskException(override val message: String) : RuntimeException(message) {
    class CheckFailException(task: ITask<*>) : TaskException("任务 ${task.name} 自检未通过")
    class CheckThrowException(task: ITask<*>, val error: Throwable) : TaskException("任务 ${task.name} 自检时发生异常")
    class RunFailException(task: ITask<*>, val error: Throwable) : TaskException("任务 ${task.name} 执行时发生异常")
    class CommentFailException(task: ITask<*>, val error: Throwable) : TaskException("任务 ${task.name} 提交时发生异常")
    class UserRunExitException(task: ITask<*>) : TaskException("任务组 ${task.name} 在运行时被手动退出")
    class UserCommentExitException(task: ITask<*>) : TaskException("任务组 ${task.name} 在提交时被手动退出")
}
