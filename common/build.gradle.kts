plugins {
    plugin.`java-conventions`
    `java-library`
}

dependencies {
    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
