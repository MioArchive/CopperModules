plugins {
    plugin.`java-conventions`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.velocity)
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":common"))
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    register("generateTemplates")

    jar {
        exclude("velocity-plugin.json")
        exclude("net/javamio/coppermodule/velocity/CopperVelocity*.class")
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-Velocity-${version}.jar"
        archiveClassifier = null
    }

    assemble {
        dependsOn(shadowJar)
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
        runDirectory = layout.projectDirectory.dir("run/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
}
