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
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.content.negotiation)
        }

        jvmMain.dependencies {
        }

        androidMain.dependencies {
        }

        nativeMain.dependencies {
        }

        appleMain.dependencies {
        }

        iosMain.dependencies {
        }

        macosMain.dependencies {
        }

        linuxMain.dependencies {
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
            artifactId = "yutorix.module.yhchat"
            version = System.getenv("VERSION")
            description = "Kotlin Multiplatform library"
            url = "https://github.com/Nyayurn/Yutori"

            developers {
                developer {
                    id = "Nyayurn"
                    name = "Yurn"
                    email = "Nyayurn@outlook.com"
                }
            }
            scm {
                url = "https://github.com/Nyayurn/Yutori"
            }
        }
    }
}