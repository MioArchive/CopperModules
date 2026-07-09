plugins {
    plugin.`java-conventions`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly(libs.paper)
    annotationProcessor(libs.lombok)
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            from(components["shadow"])
        }
    }
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-Bukkit-${version}.jar"
        archiveClassifier = null
    }

    assemble {
        dependsOn(shadowJar)
    }

    // 1.8.8 - 1.16.5 = Java 8
    // 1.17           = Java 16
    // 1.18 - 1.20.4  = Java 17
    // 1-20.5+        = Java 21
    val version = "1.21.4"
    val javaVersion = JavaLanguageVersion.of(21)

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    val sharedBukkitPlugins = runPaper.downloadPluginsSpec {
        url("https://github.com/ViaVersion/ViaVersion/releases/download/5.9.1/ViaVersion-5.9.1.jar")
        url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.9.1/ViaBackwards-5.9.1.jar")
    }

    runServer {
        minecraftVersion(version)
        runDirectory = layout.projectDirectory.dir("run/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            from(sharedBukkitPlugins)
        }

        jvmArgs = jvmArgsExternal
    }

    runPaper.folia.registerTask {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/folia/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            from(sharedBukkitPlugins)
        }

        jvmArgs = jvmArgsExternal
    }
}
