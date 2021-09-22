package samples.one

import i.task.ITask
import i.task.ITaskContext
import i.task.TaskCallBack
import i.task.TaskRollbackInfo
import i.task.modules.fifo.FIFOTaskManager
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * 单个任务发布测试
 */
class OneErrorTaskTest : ITask<String> {

    companion object {
        private val logger = LoggerFactory.getLogger(OneErrorTaskTest::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val mgr = FIFOTaskManager()
            mgr.submit(
                "first", listOf(OneErrorTaskTest()),
                TaskCallBack.builder<String>().fail {
                    logger.info("任务执行失败", it)
                }.build()
            )
            mgr.shutdown()
        }
    }

    override val key: String
        get() {
            logger.info("获取名称")
            return "task.one"
        }

    override fun check(context: ITaskContext): Boolean {
        logger.info("前置执行检查.")
        return true
    }

    override fun run(context: ITaskContext): String {
        logger.info("执行任务.")
        throw RuntimeException("Error")
    }

    override fun rollback(info: TaskRollbackInfo) {
        logger.info("触发回滚,类型：{}，错误：{}", info.type, info.error)
    }

    override fun close() {
        logger.info("任务结束")
    }

    @Test
    fun test() {
        main(arrayOf())
    }
}
