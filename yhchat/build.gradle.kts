plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.android.library)
}

group = "cn.yurn.yutorix"


kotlin {
    jvmToolchain(17)

    jvm()

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

    // Apple(IOS & MacOS)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "yutorix-module-yhchat"
            isStatic = true
        }
    }

    // Linux
    linuxX64 {
        binaries.staticLib {
            baseName = "yutorix-module-yhchat"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":yutori"))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        jvmMain.dependencies {
            api(libs.ktor.client.okhttp)
        }

        androidMain.dependencies {
            api(libs.ktor.client.okhttp)
        }

        nativeMain.dependencies {
        }

        appleMain.dependencies {
            api(libs.ktor.client.darwin)
        }

        iosMain.dependencies {
        }

        macosMain.dependencies {
        }

        linuxMain.dependencies {
            api(libs.ktor.client.curl)
        }
    }
}

android {
    namespace = "cn.yurn.yutorix.module.yhchat"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name = "Yutorix-Module-YhChat"
            version = System.getenv("VERSION")
            description = "Kotlin Multiplatform library"
            url = "https://github.com/Nyayurn/yutori"

            developers {
                developer {
                    id = "Nyayurn"
                    name = "Yurn"
                    email = "Nyayurn@outlook.com"
                }
            }
            scm {
                url = "https://github.com/Nyayurn/yutori"
            }
        }
    }
}