package i.task

enum class TaskStatus(val typeId: Int) {
    READY(0),
    CHECK_FAIL(1),
    ERROR(2),
    FINISH(3)
}
