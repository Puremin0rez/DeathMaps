plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.3.1"
}

group = "net.pl3x.bukkit.deathmaps"
version = "1.18"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    paperDevBundle("1.18-R0.1-SNAPSHOT")
}

defaultTasks("clean", "build")

tasks {
    processResources {
        val placeholders = mapOf(
            "name" to project.name,
            "group" to project.group,
            "version" to project.version
        )
        filesMatching("plugin.yml") {
            expand(placeholders)
        }
    }

    jar {
        archiveFileName.set("${project.name}-mojmap.jar")
    }

    reobfJar {
        outputJar.set(project.layout.buildDirectory.file("libs/${project.name}.jar"))
    }

    build {
        dependsOn(reobfJar)
    }
}
