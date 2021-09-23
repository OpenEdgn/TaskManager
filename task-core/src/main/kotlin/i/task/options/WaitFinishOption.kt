package i.task.options

import i.task.ITaskOption

class WaitFinishOption(value: Boolean = false) : ITaskOption<Boolean>(value) {
    override val key: String = "task.option.wait_finish"
}
