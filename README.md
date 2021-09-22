# 任务调度组件

<a href="https://github.com/OpenEdgn/TaskManager" target="_blank"><img alt="Jenkins" src="https://github.com/OpenEdgn/TaskManager/actions/workflows/build.yml/badge.svg?branch=master&color=green&style=flat-square"/></a>
<a href="https://ktlint.github.io/"><img alt="GitHub license" src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg"></a>
<a href="LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/OpenEdgn/TaskManager"></a>
<a href="https://jitpack.io/#OpenEdgn/TaskManager" target="_blank"> <img alt="JitPack" src="https://img.shields.io/jitpack/v/github/OpenEdgn/TaskManager"></a>

> 基于 [Kotlin](https://kotlinlang.org) 的任务调度组件

## 特性

1. 开箱即用
2. 极其精简，仅依赖于 [SLF4J](http://www.slf4j.org/) 和 [Kotlin](https://kotlinlang.org/)
3. 针对常用方法覆盖测试用例

## 快速开始

如何导入项目请参照 [jitpack](https://jitpack.io/#OpenEdgn/TaskManager).

一个简单的示例： [samples.kt](/task-fifo/src/test/kotlin/samples/samples.kt) .

更多示例代码在 [Samples](/task-fifo/src/test/kotlin/samples) 目录下.

## 贡献

欢迎您为本项目贡献源代码。此项目采用 [ktlint](https://ktlint.github.io) 做代码格式验证，请在提交前执行 `./gradlew ktlintFormat ktlintCheck`.

## LICENSE

请查看 [License](./LICENSE) 文件
