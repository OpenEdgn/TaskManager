package samples.many

import i.task.ITaskContext
import i.task.extra.task.SimpleTask
import i.task.extra.taskManager
import i.task.modules.fifo.FIFOTaskManager
import org.slf4j.LoggerFactory

class ManyTaskJoinTest {
    companion object {
        private val logger = LoggerFactory.getLogger(ManyTaskJoinTest::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            taskManager(FIFOTaskManager) {
                name = "fifo-test"
            }.apply {
                val value = submit<String>("test")
                    .append(TaskA())
                    .append(TaskB())
                    .append(TaskC())
                    .putOption(options.WAIT_FINISH, true)
                    .success {
                        logger.info("任务回调完成！返回: {}", it)
                    }.submit().value
                logger.info("阻塞返回数据：$value")
                shutdown()
            }
        }
    }

    class TaskA : SimpleTask<String>("taskA") {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "启动A任务")
            Thread.sleep(500)
            return "first"
        }
    }

    class TaskB : SimpleTask<String>("taskB") {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "上个任务回调：{}", context.currentGroup.lastTaskResult<Any>())
            logger.info(marker, "启动B任务")
            Thread.sleep(500)
            return "second"
        }
    }

    class TaskC : SimpleTask<String>("taskC") {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "上个任务回调：{}", context.currentGroup.lastTaskResult<Any>())
            logger.info(marker, "启动B任务")
            Thread.sleep(500)
            return "second"
        }
    }
}
