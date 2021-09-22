package i.task

interface ITaskFinishRunner {
    fun submit(runnable: Runnable)
    fun shutdown()
}
