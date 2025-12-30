plugins {
    id("java")
}

val git : String = versionBanner()
val builder : String = builder()
ext["git_version"] = git
ext["builder"] = builder

subprojects {

    apply(plugin = "java")
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://jitpack.io/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    tasks.processResources {
        filteringCharset = "UTF-8"

        // craft-engine.properties 占位符
        filesMatching("craft-engine.properties") {
            expand(rootProject.properties)
        }

        // commands.yml 和 config.yml 占位符，增加默认值防止 CI 崩溃
        val projectVersion = rootProject.properties["project_version"] ?: "0.0.1"
        val configVersion = rootProject.properties["config_version"] ?: "0.0.1"
        val langVersion = rootProject.properties["lang_version"] ?: "0.0.1"

        filesMatching(listOf("commands.yml", "config.yml")) {
            expand(
                "project_version" to projectVersion,
                "config_version" to configVersion,
                "lang_version" to langVersion
            )
        }
    }
}


fun versionBanner(): String {
    return System.getenv("GIT_COMMIT") 
        ?: try {
            project.providers.exec {
                commandLine("git", "rev-parse", "--short=8", "HEAD")
            }.standardOutput.asText.get().trim()
        } catch (e: Exception) {
            "Unknown"
        }
}

fun builder(): String {
    return System.getenv("GIT_BUILDER")
        ?: try {
            project.providers.exec {
                commandLine("git", "config", "user.name")
            }.standardOutput.asText.get().trim()
        } catch (e: Exception) {
            "Unknown"
        }
}

