package samples.one

import i.task.ITaskContext
import i.task.extra.task.SimpleUnitTask
import i.task.extra.taskManager
import i.task.modules.fifo.FIFOTaskManager
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * 单个任务发布测试
 */
class OneUnitTaskTest : SimpleUnitTask("task") {

    companion object {
        private val logger = LoggerFactory.getLogger(OneUnitTaskTest::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            taskManager(FIFOTaskManager).apply {
                submit<String>("first")
                    .append(OneUnitTaskTest())
                    .success {
                        logger.info("任务回调完成！返回: {}", it)
                    }.submit()
                shutdown()
            }
        }
    }

    @Test
    fun test() {
        main(arrayOf())
    }

    override fun run(context: ITaskContext) {
        logger.info("Hello World")
    }
}
