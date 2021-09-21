package i.task

abstract class SimpleTask<RES : Any> : ITask<RES> {

    override fun check(context: ITaskContext) = true

    override fun rollback(info: TaskRollbackInfo) {
    }

    override fun close() {
    }
}
