package i.task.modules.fifo

import i.task.ITask
import i.task.ITaskCallBack
import i.task.ITaskFinishRunner
import i.task.ITaskInfo
import i.task.ITaskManager
import i.task.ITaskOption
import i.task.ITaskStatus
import i.task.TaskException
import i.task.TaskRollbackInfo
import i.task.TaskRollbackInfo.RollbackType
import i.task.TaskStatus
import i.task.extra.TaskManagerFeature
import i.task.options.WaitFinishOption
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.util.Collections
import java.util.Optional
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock

/**
 * 先进先出单线程串行任务调度算法
 *
 *
 */
class FIFOTaskManager(
    override val name: String = "FIFO",
    threadFactory: ThreadFactory = Executors.defaultThreadFactory(),
    private val runner: ITaskFinishRunner
) : ITaskManager<FIFOOptions>, ITaskInfo {
    private val marker = MarkerFactory.getMarker("任务管理器：\"$name\"")

    private val shutdown = AtomicBoolean(false)
    private val lock = ReentrantLock()
    private val condition = lock.newCondition() // 锁
    private val taskGroups = LinkedBlockingDeque<IFIFOTaskGroup<Any>>() // 任务组
    private val tasks = Collections.synchronizedSet(HashSet<ITask<*>>())

    init {
        val thread = threadFactory.newThread {
            while (taskGroups.isNotEmpty() || shutdown.get().not()) {
                if (taskGroups.isEmpty()) {
                    lock.lock()
                    condition.await()
                    lock.unlock()
                } else {
                    runTaskGroup()
                }
            }
            clear()
        }
        thread.name = name
        thread.start()
        logger.debug(marker, "工作线程已启动.")
    }

    /**
     * 生命周期结束，清除
     */
    private fun clear() {
        logger.debug(marker, "任务管理器实例已退出.")
        runner.shutdown()
    }

    private fun runTaskGroup() { // 任务队列
        var error = Optional.empty<Throwable>()
        val taskGroup: IFIFOTaskGroup<Any> = taskGroups.pollFirst() ?: return
        try {
            taskGroup.run() // 执行任务
        } catch (e: Throwable) {
            try { // 发生错误，触发任务回滚
                logger.debug(marker, "任务组 \"{}\" 发生错误：\"{}\"，执行回滚.", taskGroup.name, e.message ?: "")
                error = Optional.of(e)
                val type = when (e) {
                    is TaskException.CheckFailException -> {
                        RollbackType.CURRENT_CHECK_ERROR
                    }
                    is TaskException.UserExitException -> {
                        RollbackType.USER_CANCEL
                    }
                    else -> {
                        RollbackType.CURRENT_RUN_ERROR
                    }
                }
                taskGroup.cancel(TaskRollbackInfo(type, error))
            } catch (_: Throwable) {
            }
        } finally {
            try {
                taskGroup.close()
            } catch (_: Throwable) {
            }
        }
        taskGroup.callBack(
            runner, error
        )
        condition.tryLock {
            tasks.removeAll(taskGroup.tasks)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <RES : Any> submit(
        name: String,
        task: List<ITask<*>>,
        options: List<ITaskOption<*>>,
        call: ITaskCallBack<RES>
    ): ITaskStatus<RES> {
        val result = condition.tryLock {
            if (task.isEmpty()) {
                throw NullPointerException("没有任务！")
            }
            if (shutdown.get()) {
                throw RuntimeException("此任务管理器已销毁,无法添加任务!")
            }
            if (Collections.disjoint(this.tasks, task)) { // 排除重复任务
                this.tasks.addAll(task)
                val fifoTaskGroup = FIFOTaskGroup(name, task, call)
                taskGroups.addLast(fifoTaskGroup as IFIFOTaskGroup<Any>)
                lock.lock()
                condition.signalAll()
                lock.unlock()
                fifoTaskGroup.taskStatusCall
            } else {
                throw RuntimeException("某些任务已存在于队列中，在此任务停止之前不允许添加相同任务！")
            }
        }

        if (this.options.getConfig(options, WaitFinishOption::class)) {
            while (result.status != TaskStatus.FINISH && result.status != TaskStatus.ERROR) {
                Thread.sleep(50)
            }
        }
        return result
    }

    override val options: FIFOOptions = FIFOOptions

    override fun shutdown() = condition.tryLock {
        logger.debug(marker, "收到销毁信号，从此刻开始不再接收新任务.")
        shutdown.set(true)
        lock.lock()
        condition.signalAll()
        lock.unlock()
    }

    override val process: Float
        get() {
            var process = 0f
            taskGroups.forEach { process += it.process }
            return process / taskGroups.size
        }

    override val size: Int
        get() {
            var size = 0
            taskGroups.forEach { size += it.size }
            return size
        }
    override val taskInfo: ITaskInfo = this
    private fun <T : Any?> Any.tryLock(function: () -> T): T {
        return synchronized(this) {
            function()
        }
    }

    class Config : TaskManagerFeature.Configuration {
        override var name: String = "FIFO"
        var threadFactory: ThreadFactory = Executors.defaultThreadFactory()
        var runner: ITaskFinishRunner = InternalTaskFinishRunner()
    }

    internal class InternalTaskFinishRunner : ITaskFinishRunner {
        private val threadPool = Executors.newCachedThreadPool()
        override fun submit(runnable: Runnable) {
            threadPool.submit(runnable)
        }

        override fun shutdown() {
            threadPool.shutdown()
        }
    }

    companion object Feature : TaskManagerFeature<FIFOOptions, FIFOTaskManager, Config> {
        override val config: Config = Config()
        private val logger = LoggerFactory.getLogger(FIFOTaskManager::class.java)
        override fun newTaskManager(config: Config): FIFOTaskManager {
            return FIFOTaskManager(config.name, config.threadFactory, config.runner)
        }
    }
}
