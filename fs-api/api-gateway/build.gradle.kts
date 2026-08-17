plugins {
    alias(catalogue.plugins.spring.boot)
    alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
    alias(catalogue.plugins.kotlin.spring)
}

dependencies {
    api(libs.f2.spring.boot.starter.function.http)

    implementation(project(":fs-api:api-config"))
    implementation(project(":fs-s2:file:fs-file-app"))
    implementation(libs.reflections)
}
