package i.task.extra.task

import i.task.ITask
import i.task.ITaskContext
import i.task.TaskRollbackInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * 创建简单的任务
 *
 * @param T : Any 任务返回值
 * @property key String 任务ID
 * @constructor
 */
abstract class SimpleTask<T : Any>(override val key: String) : ITask<T> {
    protected val marker: Marker by lazy { MarkerFactory.getMarker("Task:$key") }
    protected val logger: Logger by lazy { LoggerFactory.getLogger(javaClass) }
    override fun check(context: ITaskContext): Boolean {
        return true
    }

    override fun rollback(info: TaskRollbackInfo) {
    }

    override fun close() {
    }
}
