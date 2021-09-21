package i.task

/**
 * 任务回调函数
 */
class TaskCallBack<RES : Any> private constructor(
    val success: (RES) -> Unit,
    val fail: (Throwable) -> Unit,
    val putHook: (ITaskStatus<RES>) -> Unit = {}
) {

    companion object {
        /**
         * 只处理成功回调
         */
        fun <T : Any> success(call: (T) -> Unit) = TaskCallBack(success = call, fail = {})

        /**
         * 只处理失败回调
         */
        fun <T : Any> fail(fail: (Throwable) -> Unit) = TaskCallBack<T>(success = {}, fail = fail)

        /**
         * 自定义任务处理建造者
         */
        fun <RES : Any> builder() = TaskCallBackBuilder<RES>()

        /**
         *  等待任务结束返回
         */
        fun <T : Any> join(): TaskCallBack<T> {

            return builder<T>().putHook {
                while (true) {
                    if (it.status == TaskStatus.FINISH || it.status == TaskStatus.ERROR) {
                        break
                    }
                    Thread.sleep(100)
                }
            }.build()
        }

        class TaskCallBackBuilder<RES : Any> {

            private var success: (RES) -> Unit = {}
            private var fail: (Throwable) -> Unit = {}
            private var putHook: (ITaskStatus<RES>) -> Unit = {}

            fun success(success: (RES) -> Unit) = kotlin.run {
                this.success = success
                this
            }

            fun fail(fail: (Throwable) -> Unit) = kotlin.run {
                this.fail = fail
                this
            }

            internal fun putHook(hook: (ITaskStatus<RES>) -> Unit) = kotlin.run {
                this.putHook = hook
                this
            }

            fun build() = TaskCallBack(success, fail, putHook)
        }
    }
}
