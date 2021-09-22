package samples.many

import i.task.ITaskContext
import i.task.TaskCallBack
import i.task.modules.fifo.FIFOTaskManager
import org.slf4j.LoggerFactory

class ManyTaskUserExitTest {
    companion object {
        private val logger = LoggerFactory.getLogger(ManyTaskUserExitTest::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val mgr = FIFOTaskManager()
            val submit = mgr.submit(
                "test", listOf(TaskA(), TaskB(), TaskC()),
                TaskCallBack.success {
                    logger.info("任务回调成功！返回：{}.", it)
                }
            )
            Thread.sleep(1000)
            submit.cancel()
            mgr.shutdown()
        }
    }

    class TaskA : SimpleTask<String>() {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "启动A任务")
            Thread.sleep(4000)
            return "first"
        }
    }

    class TaskB : SimpleTask<String>() {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "上个任务回调：{}", context.currentGroup().lastTaskResult<Any>())
            logger.info(marker, "启动B任务")
            Thread.sleep(4000)
            return "second"
        }
    }

    class TaskC : SimpleTask<String>() {
        override fun run(context: ITaskContext): String {
            logger.info(marker, "上个任务回调：{}", context.currentGroup().lastTaskResult<Any>())
            logger.info(marker, "启动B任务")
            Thread.sleep(4000)
            return "second"
        }
    }
}
