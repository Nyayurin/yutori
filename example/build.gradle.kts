import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    application
}

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass = "MainKt"
        }
        application {
            mainClass = "MainKt"
        }
    }

    /*@OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        binaries.executable()
    }

    js {
        nodejs()
        binaries.executable()
    }*/

    mingwX64("native").binaries.executable()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":yutori"))
            implementation(project(":yhchat"))
        }

        jvmMain.dependencies {

        }

        /*jsMain.dependencies {

        }*/
    }
}