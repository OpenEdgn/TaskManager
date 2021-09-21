package i.task.modules.fifo

import i.task.ITaskContext
import i.task.SimpleTask
import i.task.TaskCallBack
import org.junit.jupiter.api.Test

internal class FIFOTaskManagerTest {
    @Test
    fun test() {
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
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FIFOTaskManagerTest().test()
        }
    }
}
