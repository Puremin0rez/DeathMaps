plugins {
    id("java")
}

group = "net.pl3x.bukkit.deathmaps"
version = "1.17"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(group = "org.spigotmc", name = "spigot", version = "1.17-R0.1-SNAPSHOT")
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
        archiveFileName.set("${project.name}.jar")
    }
}
