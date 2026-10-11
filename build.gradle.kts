plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.24"
}

group = "net.pl3x.bukkit.deathmaps"
version = "26.3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("26.3.build.174-beta")
}

defaultTasks("clean", "build")

tasks {
    wrapper {
        gradleVersion = "9.8.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    processResources {
        val placeholders = mapOf(
            "name" to project.name,
            "group" to project.group,
            "version" to project.version
        )
        inputs.properties(placeholders)
        filesMatching("plugin.yml") {
            expand(placeholders)
        }
    }

    jar {
        archiveFileName.set("${project.name}.jar")
    }
}
