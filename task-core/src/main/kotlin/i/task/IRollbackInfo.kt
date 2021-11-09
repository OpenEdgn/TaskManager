package i.task

/**
 *  日志错误回滚原因信息
 */
interface IRollbackInfo {

    /**
     * 回滚触发原因
     */
    val type: RollbackType

    /**
     * 回滚触发详情
     */
    val error: TaskException
}
