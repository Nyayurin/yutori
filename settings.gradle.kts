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
	@Suppress("UnstableApiUsage")
	repositories {
	    google()
	    mavenCentral()
	}
}