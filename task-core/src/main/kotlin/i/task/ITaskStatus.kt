package i.task

import java.util.Optional

/**
 * 任务信息
 */
interface ITaskStatus<RES : Any> {
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
     * 撤销/终止任务
     *
     */
    fun cancel()
}
