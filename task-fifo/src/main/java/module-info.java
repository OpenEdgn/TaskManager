module edgn.utils.task.fifo {
    requires kotlin.reflect;
    requires kotlin.stdlib;
    requires org.slf4j;
    requires edgn.utils.task.core;
    requires edgn.utils.task.extra;
    exports i.task.modules.fifo;
    opens i.task.modules.fifo;

}
