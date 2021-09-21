package i.task

import java.util.Optional

/**
 *  日志错误回滚原因信息
 */
data class TaskRollbackInfo(

    /**
     * 回滚触发原因
     */
    val type: RollbackType,

    /**
     * 回滚触发详情
     */
    val error: Optional<Throwable> = Optional.empty()
) {

    enum class RollbackType {
        CURRENT_CHECK_ERROR,
        NEXT_CHECK_ERROR,
        CURRENT_RUN_ERROR,
        NEXT_RUN_ERROR,
        USER_CANCEL
    }
}
