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
     * 当任务正在执行或处于准备队列中时，
     * 使用此函数将标记任务组终止执行,
     *
     *
     */
    fun cancel()
}
