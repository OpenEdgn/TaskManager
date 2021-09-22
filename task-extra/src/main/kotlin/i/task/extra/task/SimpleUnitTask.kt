package i.task.extra.task

import java.util.UUID

/**
 * 创建单个不带返回值的任务
 */
abstract class SimpleUnitTask(key: String = UUID.randomUUID().toString()) : SimpleTask<Unit>(key)
