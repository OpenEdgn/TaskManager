module edgn.utils.task.extra {
    requires kotlin.reflect;
    requires kotlin.stdlib;
    requires org.slf4j;
    requires edgn.utils.task.core;
    opens i.task.extra;
    exports i.task.extra;
    opens i.task.extra.task;
    exports i.task.extra.task;
    opens i.task.extra.options;
    exports i.task.extra.options;
}
