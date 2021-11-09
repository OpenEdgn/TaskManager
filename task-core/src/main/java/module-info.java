import i.task.ITaskManager;

module edgn.utils.task.core {
    requires kotlin.reflect;
    requires kotlin.stdlib;
    opens i.task;
    exports i.task;
    uses ITaskManager;
}
