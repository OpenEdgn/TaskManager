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
        OTHER_CHECK_ERROR,
        CURRENT_RUN_ERROR,
        OTHER_RUN_ERROR,
        USER_CANCEL
    }

    val isCurrentError: Boolean
        get() = type in listOf(
            RollbackType.CURRENT_CHECK_ERROR,
            RollbackType.CURRENT_RUN_ERROR,
        )
}
