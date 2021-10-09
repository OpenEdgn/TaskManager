package i.task

/**
 * 任务回滚原因
 */
enum class TaskRollbackType {
    CHECK_ERROR,
    RUN_ERROR,
    COMMENT_ERROR,
    USER_CANCEL
}
