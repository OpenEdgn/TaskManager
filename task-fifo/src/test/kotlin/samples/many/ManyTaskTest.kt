package samples.many

import i.task.ITaskContext
import i.task.TaskCallBack
import i.task.modules.fifo.FIFOTaskManager
import org.slf4j.LoggerFactory

class ManyTaskTest {
    companion object {
        private val logger = LoggerFactory.getLogger(ManyTaskTest::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val mgr = FIFOTaskManager()
            mgr.submit<String>(
                "test", listOf(TaskA(), TaskB()),
                TaskCallBack.success {
                    logger.info("任务回调完成！返回: {}", it)
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
            return "second"
        }
    }
}
