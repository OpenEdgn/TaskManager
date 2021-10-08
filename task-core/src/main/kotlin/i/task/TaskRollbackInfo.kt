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
        CHECK_ERROR,
        RUN_ERROR,
        COMMENT_ERROR,
        USER_RUN_CANCEL,
        USER_COMMENT_CANCEL
    }

    val isRunError: Boolean
        get() = type in listOf(
            RollbackType.RUN_ERROR,
        )
    val isCheckError: Boolean
        get() = type in listOf(
            RollbackType.CHECK_ERROR,
        )
    val isCommentError: Boolean
        get() = type in listOf(
            RollbackType.COMMENT_ERROR,
        )
    val isUserRunCancel: Boolean
        get() = type in listOf(
            RollbackType.USER_RUN_CANCEL,
        )
    val isUserCommentCancel: Boolean
        get() = type in listOf(
            RollbackType.USER_COMMENT_CANCEL,
        )
}
