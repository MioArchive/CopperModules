plugins {
    plugin.`java-conventions`
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
