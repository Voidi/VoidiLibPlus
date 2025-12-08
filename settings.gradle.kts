enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "stdlibplus"

pluginManagement {
	repositories {
		mavenLocal()
		gradlePluginPortal()
		maven ( url = "https://maven.neoforged.net/releases" )
	}
}

plugins {
	// Apply the foojay-resolver plugin to allow automatic download of JDKs
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
