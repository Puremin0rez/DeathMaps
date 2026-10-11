import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
  kotlin("jvm") version "2.4.21"
  id("com.ncorti.ktfmt.gradle") version "0.27.0"
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

group = "net.pl3x.bukkit.deathmaps"

version = "26.3"

kotlin { jvmToolchain(25) }

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies { paperweight.paperDevBundle("26.3.build.174-beta") }

defaultTasks("clean", "build")

tasks {
  wrapper {
    gradleVersion = "9.8.1"
    distributionType = Wrapper.DistributionType.ALL
  }

  processResources {
    val placeholders =
        mapOf(
            "name" to project.name,
            "group" to project.group,
            "version" to project.version,
            "kotlinVersion" to getKotlinPluginVersion(),
        )
    inputs.properties(placeholders)
    filesMatching("plugin.yml") { expand(placeholders) }
  }

  jar { archiveFileName.set("${project.name}.jar") }
}
