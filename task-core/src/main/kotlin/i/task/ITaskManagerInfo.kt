package i.task

/**
 * 任务管理器信息
 */
interface ITaskManagerInfo {
    /**
     * 任务总体进度
     */
    val process: Float

    /**
     * 任务组数目 （包括正在运行）
     */
    val size: Int
}
