package i.task

/**
 *  日志错误回滚原因信息
 */
interface ITaskRollbackInfo {

    /**
     * 回滚触发原因
     */
    val type: RollbackType

    /**
     * 回滚触发详情
     */
    val error: Throwable

    enum class RollbackType {
        CURRENT_CHECK_ERROR,
        NEXT_CHECK_ERROR,
        CURRENT_RUN_ERROR,
        NEXT_RUN_ERROR,
    }
}
