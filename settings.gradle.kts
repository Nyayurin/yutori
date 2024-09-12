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

project(":satori").name = "yutorix-module-satori"
project(":yhchat").name = "yutorix-module-yhchat"