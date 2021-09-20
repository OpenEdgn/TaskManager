package i.task

import java.util.Optional

/**
 * 任务信息
 */
interface ITaskStatus<T : Any, RES : Any> {
    /**
     * 任务当前状态
     */
    val status: TaskStatus

    /**
     * 获取任务结果
     */
    val value: Optional<RES>

    /**
     * 任务进度
     */
    val process: Float

    /**
     * 撤销任务
     *
     * 注意：仅在任务未执行时可撤销
     */
    fun cancel(): Boolean
}
