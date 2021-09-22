package samples.many

import i.task.ITaskContext
import i.task.extra.task.SimpleTask
import i.task.extra.taskManager
import i.task.modules.fifo.FIFOTaskManager
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ManyTaskUserExitTest {
    companion object {
        private val logger = LoggerFactory.getLogger(ManyTaskUserExitTest::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            taskManager(FIFOTaskManager).apply {
                val submit = submit<String>("test")
                    .append(TaskA())
                    .append(TaskB())
                    .append(TaskC())
                    .success {
                        logger.info("任务回调完成！返回: {}", it)
                    }.submit()
                Thread.sleep(1000)
                submit.cancel()
                shutdown()
            }
        }
    }

    class TaskA : SimpleTask<String>("taskA") {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "启动A任务")
            Thread.sleep(4000)
            return "first"
        }
    }

    class TaskB : SimpleTask<String>("taskB") {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "上个任务回调：{}", context.currentGroup.lastTaskResult<Any>())
            logger.info(marker, "启动B任务")
            Thread.sleep(4000)
            return "second"
        }
    }

    class TaskC : SimpleTask<String>("taskC") {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "上个任务回调：{}", context.currentGroup.lastTaskResult<Any>())
            logger.info(marker, "启动B任务")
            Thread.sleep(4000)
            return "second"
        }
    }

    @Test
    fun test() {
        main(arrayOf())
    }
}
