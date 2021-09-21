package samples.many

import i.task.ITask
import i.task.ITaskContext
import i.task.TaskRollbackInfo
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory

abstract class SimpleTask<T : Any> : ITask<T> {
    protected val logger = LoggerFactory.getLogger(javaClass)
    protected val marker = MarkerFactory.getMarker(logger.name)
    override val key: String = "task.many.a"
    override fun check(context: ITaskContext): Boolean {
        logger.info(marker, "检查")
        return true
    }

    override fun rollback(info: TaskRollbackInfo) {
        logger.info(marker, "回滚")
    }

    override fun close() {
        logger.info(marker, "销毁")
    }
}
