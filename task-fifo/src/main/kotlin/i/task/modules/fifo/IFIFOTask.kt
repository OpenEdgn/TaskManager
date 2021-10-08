package i.task.modules.fifo

import i.task.ITask

/**
 * 内部任务包装
 *
 * @property task ITask<*>
 * @property process Float
 */
interface IFIFOTask {
    /**
     * 任务实例
     */
    val task: ITask<*>

    /**
     * 任务进度
     */
    val process: Float
}
