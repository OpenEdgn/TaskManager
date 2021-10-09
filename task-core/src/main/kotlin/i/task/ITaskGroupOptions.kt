package i.task

import kotlin.reflect.KClass

/**
 * 任务提交时可配置选项
 */
interface ITaskGroupOptions {
    /**
     * 此任务管理器所支持的全部选项
     */
    val listOptions: Map<KClass<out ITaskGroupOption<*>>, ITaskGroupOption<*>>
}
