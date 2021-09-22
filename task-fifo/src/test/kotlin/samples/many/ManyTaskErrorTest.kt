package samples.many

import i.task.ITaskContext
import i.task.TaskCallBack
import i.task.modules.fifo.FIFOTaskManager
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ManyTaskErrorTest {
    companion object {
        private val logger = LoggerFactory.getLogger(ManyTaskErrorTest::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val mgr = FIFOTaskManager()
            mgr.submit(
                "test", listOf(TaskA(), TaskB()),
                TaskCallBack.fail {
                    logger.info("任务回调失败！", it)
                }
            )
            mgr.shutdown()
        }
    }

    class TaskA : SimpleTask<String>() {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "启动A任务")
            return "first"
        }
    }

    class TaskB : SimpleTask<String>() {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "上个任务回调：{}", context.currentGroup().lastTaskResult<Any>())
            logger.info(marker, "启动B任务")
            throw RuntimeException("触发任务回滚.")
        }
    }

    @Test
    fun test() {
        main(arrayOf())
    }
}
