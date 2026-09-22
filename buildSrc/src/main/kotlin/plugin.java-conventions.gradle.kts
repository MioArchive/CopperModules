plugins {
    `java-library`
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

pluginManager.withPlugin("com.gradleup.shadow") {
    (components["java"] as AdhocComponentWithVariants)
        .withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) { skip() }
}

if (System.getenv("JITPACK") == "true") {
    tasks.withType<GenerateModuleMetadata>().configureEach { enabled = false }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "coppermodule-${project.name}"

            pom {
                name = "CopperModule ${project.name.replaceFirstChar { it.uppercase() }}"
                description = "Modular library for Minecraft plugins (${project.name})."
                url = "https://github.com/MioArchive/CopperModules"
                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.txt"
                    }
                }
                scm {
                    url = "https://github.com/MioArchive/CopperModules"
                    connection = "scm:git:https://github.com/MioArchive/CopperModules.git"
                }
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
