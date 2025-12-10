import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
	`java-library`
	alias(libs.plugins.kotlin)
//	kotlin("jvm") version "2.2.21"
	alias(libs.plugins.moddev)
	idea
	id("maven-publish")
}

tasks.named<Wrapper>("wrapper") {
	// Define wrapper values here so as to not have to always do so when updating gradlew.properties.
	// Switching this to Wrapper.DistributionType.ALL will download the full gradle sources that comes with
	// documentation attached on cursor hover of gradle classes and methods. However, this comes with increased
	// file size for Gradle. If you do switch this to ALL, run the Gradle wrapper task twice afterwards.
	// (Verify by checking gradle/wrapper/gradle-wrapper.properties to see if distributionUrl now points to `-all`)
	distributionType = Wrapper.DistributionType.BIN
}

val mod_id: String by project
val mod_name: String by project
val mod_version: String by project
val mod_group_id: String by project

version = mod_version
group = mod_group_id

// This block of code expands all declared replace properties in the specified resource targets.
// A missing property will result in an error.
val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
  val mod_license: String by project
  val mod_authors: String by project
	val mod_credits: String by project
  val mod_description: String by project
	val replaceProperties = mapOf(
			"minecraft_version"       to libs.versions.minecraft.get(),
			"minecraft_version_range" to libs.versions.minecraftRange.get(),
			"neo_version"             to libs.versions.neoforge.get(),
			"neo_version_range"       to libs.versions.neoforgeRange.get(),
			"kff_version"             to libs.versions.kff.get(),
			"kff_version_range"       to libs.versions.kffRange.get(),
			"loader_version_range"    to libs.versions.loaderRange.get(),
			"mod_id"                  to mod_id,
			"mod_name"                to mod_name,
			"mod_license"             to mod_license,
			"mod_version"             to mod_version,
			"mod_authors"             to mod_authors,
			"mod_credits"             to mod_credits,
			"mod_description"         to mod_description
	)
	inputs.properties(replaceProperties)
	expand(replaceProperties)
	from("src/main/templates")
	into("build/generated/sources/modMetadata")
}

repositories {
	mavenLocal()
	maven {
		name = "Kotlin for Forge"
		setUrl("https://thedarkcolour.github.io/KotlinForForge/")
	}
	maven {
		name = "Modrinth"
		setUrl("https://api.modrinth.com/maven")
	}
}

base {
	archivesName = mod_id
}

java {
	withJavadocJar()
	withSourcesJar()
}

// Mojang ships Java 21 to end users starting in 1.20.5, so mods should target Java 21.
java.toolchain.languageVersion = JavaLanguageVersion.of(21)

kotlin.compilerOptions {
	jvmTarget.set(JvmTarget.JVM_21)
	freeCompilerArgs.add("-Xjvm-default=all")
}

sourceSets {
	main {
		resources {
			srcDir("src/generated/resources")
      // Include the output of "generateModMetadata" as an input directory for the build this works with both building through Gradle and the IDE.
      srcDir(generateModMetadata)
		}
	}
	register("datagen") {
		compileClasspath += main.get().output + main.get().compileClasspath
		runtimeClasspath += main.get().output + main.get().runtimeClasspath
		compileClasspath += main.get().output
		runtimeClasspath += main.get().output
	}
}

neoForge {
	// Specify the version of NeoForge to use.
	version = libs.versions.neoforge.get()

	parchment {
		mappingsVersion = libs.versions.parchment.get()
		minecraftVersion = libs.versions.minecraft.get()
	}

	mods {
		// define mod <-> source bindings
		// these are used to tell the game which sources are for which mod
		// mostly optional in a single mod project but multi mod projects should define one per mod
		register(mod_id) {
			sourceSet(sourceSets.main.get())
			sourceSet(sourceSets.named("datagen").get())
		}
	}

	runs {
		configureEach {
			// Recommended logging data for a userdev environment. The markers can be added/remove as needed separated by commas.
			// "SCAN": For mods scan.
			// "REGISTRIES": For firing of registry events.
			// "REGISTRYDUMP": For getting the contents of all registries.
			systemProperty("forge.logging.markers", "REGISTRIES")
			
			// Recommended logging level for the console
			// You can set various levels here. Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
			logLevel = org.slf4j.event.Level.DEBUG
		}
		
		register("client") {
			client()
			programArguments.addAll("--quickPlaySingleplayer", "New World")
			if (project.file("clientLog4j2.xml").exists())
				loggingConfigFile = project.file("clientLog4j2.xml")

			// Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
			systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
		}

		register("server") {
			server()
			programArgument("--nogui")
			systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
		}

		// This run config launches GameTestServer and runs all registered gametests, then exits.
		// By default, the server will crash when no gametests are provided.
		// The gametest system is also enabled by default for other run configs under the /test command.
		register("gameTestServer") {
			type = "gameTestServer"
			systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
		}

		register("data") {
			clientData()
			sourceSet = sourceSets.named("datagen").get()
			
			// Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
			programArguments.addAll("--mod", mod_id, "--all", "--output", file("src/generated/resources/").absolutePath, "--existing", file("src/main/resources/").absolutePath)
		}

		configureEach {
			if (!gameDirectory.asFile.get().exists()) {
				gameDirectory.asFile.get().mkdirs()
			}
		}
	}
}

// Sets up a dependency configuration called "localRuntime".
// This configuration should be used instead of "runtimeOnly" to declare a dependency that will be present for runtime testing but that is "optional", meaning it will not be pulled by dependents of this mod.
configurations {
	val localRuntime = register("localRuntime")
	named("runtimeClasspath").extendsFrom(localRuntime)
}

dependencies {
	// Since Kotlin is its own JVM language with its own standard library, a custom language loader needs to be used, i.e. KFF
	implementation(libs.kff)
}

// To avoid having to run "generateModMetadata" manually, make it run on every project reload
neoForge.ideSyncTask(generateModMetadata)

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}

publishing {
	publications {
		register<MavenPublication>("maven") {
			from(components["java"])
		}
	}
  repositories {
    maven {
      name = "GitHubPackages"
      setUrl("https://maven.pkg.github.com/Voidi/${mod_name}")
      credentials {
        username = System.getenv("GITHUB_USERNAME")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
	}
}