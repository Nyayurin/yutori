plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.serialization) apply false
}

subprojects {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/Nyayurn/yutori")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}