plugins {
    alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
    alias(catalogue.plugins.fixers.gradle.publish)
}

dependencies {
    implementation(libs.f2.dsl.cqrs)
    api(libs.f2.spring.boot.exception.http)
    api(project(":fs-s2:file:fs-file-client"))
    implementation(libs.bundles.ktor.client)
    api(libs.spring.web)
}
