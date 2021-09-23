module edgn.utils.task.core {
    requires kotlin.reflect;
    requires kotlin.stdlib;
    requires org.slf4j;
    opens i.task;
    exports i.task;
    opens i.task.options;
    exports i.task.options;
}
