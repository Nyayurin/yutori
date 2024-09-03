rootProject.name = "Yutori"
include(":yutori")
includeBuild("convention-plugins")

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
include(":example")
include(":satori")
include(":yhchat")
include(":application")