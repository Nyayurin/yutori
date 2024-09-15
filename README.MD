<div align="center">

# Yutori

一个支持多聊天平台的 Kotlin Multiplatform 通讯软件开发框架

| Module                | Platforms                                                                                                                                  |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| yutori                | JVM(17), Android(17), JS(Browser, Node), WasmJS(Browser, Node), IOS(x64, Arm64, SimulatorArm64), MacOS(x64, Arm64), Linux(x64), Mingw(x64) |
| yutorix-module-satori | JVM(17), Android(17), JS(Browser, Node), WasmJS(Browser, Node), IOS(x64, Arm64, SimulatorArm64), MacOS(x64, Arm64), Linux(x64), Mingw(x64) |
| yutorix-module-yhchat | JVM(17), Android(17), IOS(x64, Arm64, SimulatorArm64), MacOS(x64, Arm64), Linux(x64)                                                       |

</div>

## 如何使用

### 从 Kotlin Multiplatform 项目开始

通过 https://github.com/settings/tokens/new 创建一个属于你的 Github 个人访问 token<br>
根据需求选择适合自己的生效期(Expiration)<br>
请确保至少包含一个 `read:packages` 权限<br>
请勿将此 token 公开给其他人<br>
如果你已经有一个了, 可以跳过该步骤

添加仓库

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Nyayurn/Yutori")
        credentials {
            username = project.findProperty("gpr.username") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.token") as String? ?: System.getenv("TOKEN")
        }
    }
}
```

修改 gradle.properties, 将你 Github 账号的 username 及刚刚创建的 token 填进去

```properties
gpr.username = username
gpr.token = token
```

引入依赖

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.yutori)
            implementation(libs.yutorix.module.satori) // 可选
            implementation(libs.yutorix.module.yhchat) // 可选
        }
    }
}
```

```toml
[versions]
yutori = "version"

[libraries]
yutori = { module = "cn.yurn.yutori:yutori", version.ref = "yutori" }
yutorix-module-satori = { module = "cn.yurn.yutorix:yutorix-module-satori", version.ref = "yutori" }
yutorix-module-yhchat = { module = "cn.yurn.yutorix:yutorix-module-yhchat", version.ref = "yutori" }
```

请从 https://github.com/Nyayurn?tab=packages&repo_name=Yutori 获取版本<br>
对于稳定版, 它的版本号应该和 Release 中的 tag 一致<br>
对于开发板, 它的版本号应该和 Commit 的完整 SHA 值一致<br>
要获取一个 Commit 的完整 SHA 值, 请在 Commits 页面对应 Commit 处点击 Commit ID 右侧的复制按钮(Copy full SHA for *******)

编写代码

commonMain

```kotlin
suspend fun app() = coroutineScope {
    // 通过 builder 使用 DSL 构造一个 Yutori 对象
    val yutori = yutori {
        // 安装你需要的适配器
        install(Adapter.Satori) {
            // 在代码块内配置适配器
            host = "127.0.0.1"
            port = 5500
            token = "token"
        }
        install(Adapter.YhChat) {
            token = "token"
            selfId = "self_id"
        }
        // 通用适配器设置
        adapter {
            // 设置监听器
            listening {
                // 监听消息发送
                message.created {
                    // event.message.content 是一个 List<MessageElement> 对象, 通过过滤获得所有纯文本并拼接成字符串
                    if (event.message.content.filterIsInstance<Text>().joinToString("") { it.text } == "test") {
                        // 调用 actions.message.create 来发送一条消息
                        actions.message.create(
                            channel_id = event.channel.id,
                            // 通过消息 builder 使用 DSL 构造一条消息
                            content = {
                                // 添加一个纯文本
                                text { "test done!" }
                            }
                        )
                    }
                }
            }
        }
    }
    // 启动 Yutori
    yutori.start()
}
```

jvmMain

```kotlin
fun main() {
    runBlocking {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            bot()
        }.join()
    }
}
```

至此, 你已经学会了 Yutori 的基本使用, 接下来请慢慢探索 Yutori 的无限可能吧!

### 从 Kotlin/JVM 项目开始

Coming soon...