import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.android.library)
    id("maven-publish")
}

group = "cn.yurin.yutori"

kotlin {
    jvmToolchain(8)

    jvm()

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

    js {
        browser {
            webpackTask {
                mainOutputFileName = "yutori.js"
            }
        }
        nodejs()
        binaries.library()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            webpackTask {
                mainOutputFileName = "yutori.js"
            }
        }
        nodejs()
        binaries.library()
    }

    // Apple(IOS & MacOS)
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "yutori"
            isStatic = true
        }
    }

    // Linux
    listOf(
        linuxX64(),
        linuxArm64(),
    ).forEach {
        it.binaries.staticLib {
            baseName = "yutori"
        }
    }

    // Windows
    mingwX64 {
        binaries.staticLib {
            baseName = "yutori"
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            api(libs.kotlinx.serialization.json)
            api(libs.kermit)
        }
    }
}

android {
    namespace = "cn.yurin.yutori"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name = "Yutori"
            version = System.getenv("VERSION")
            description = "Kotlin Multiplatform library"
            url = "https://github.com/Nyayurin/yutori"

            developers {
                developer {
                    id = "Nyayurin"
                    name = "Yurin"
                    email = "Nyayurn@outlook.com"
                }
            }
            scm {
                url = "https://github.com/Nyayurin/yutori"
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/Nyayurin/yutori")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}