package i.task.modules.fifo

import i.task.ITaskContext
import i.task.SimpleTask
import i.task.TaskCallBack
import org.junit.jupiter.api.Test
import java.util.Optional

internal class FIFOTaskManagerTest {
    @Test
    fun simpleTest() {
        class TaskA(override val name: String = "read") : SimpleTask<String>() {
            override fun run(context: ITaskContext): String {
                println("Hello World")
                return "OK"
            }
        }

        class TaskB(override val name: String = "read") : SimpleTask<String>() {
            override fun run(context: ITaskContext): String {
                println(context.currentGroup().lastTaskResult<String>().get())
                return "OK x2"
            }
        }

        val fifoTaskManager = FIFOTaskManager()
        fifoTaskManager.submit(
            "Hello Tasks", listOf(TaskA(), TaskB()),
            call = TaskCallBack.success<String> {
                println(it)
            }
        )
        println("完成")
        fifoTaskManager.shutdown()
    }

    @Test
    fun testSleep() {
        class TaskC(override val name: String = "read") : SimpleTask<String>() {
            override fun run(context: ITaskContext): String {
                Thread.sleep(1000)
                println("结束")
                return "SLEEP ok"
            }
        }

        val fifoTaskManager = FIFOTaskManager()
        val submit: Optional<String> = fifoTaskManager.submit<String>(
            "data2", listOf(TaskC()),
            TaskCallBack.join()
        ).value
        println(submit)
        fifoTaskManager.shutdown()
        Thread.sleep(2000)
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FIFOTaskManagerTest().testSleep()
        }
    }
}
