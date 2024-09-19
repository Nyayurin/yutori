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
include(":yhchat")

project(":yhchat").name = "yutorix-module-yhchat"