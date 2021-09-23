package i.task

interface ITaskCallBack<RES : Any> {
    val success: (RES) -> Unit
    val fail: (Throwable) -> Unit
}
