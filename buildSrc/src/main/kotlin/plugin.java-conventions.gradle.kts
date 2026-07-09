plugins {
    java
    `maven-publish`
}

group = rootProject.group
version = rootProject.version
description = rootProject.description

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.1.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    disableAutoTargetJvm()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MioArchive/CopperModules")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
                password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.key").orNull
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    processResources {
        inputs.property("version", version)
        inputs.property("description", description ?: "")
        filesMatching(listOf("plugin.yml", "velocity-plugin.json")) {
            expand(
                mapOf(
                    "version" to version,
                    "description" to (description ?: "")
                )
            )
        }
    }

    defaultTasks("build")
}
