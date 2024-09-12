rootProject.name = "Yutori"
include(":yutori")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
include(":satori")
include(":yhchat")
//include(":example")
//include(":application")

project(":satori").name = "yutorix-module-satori"
project(":yhchat").name = "yutorix-module-yhchat"