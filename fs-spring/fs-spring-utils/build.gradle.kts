plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    id("io.komune.fixers.gradle.publish")
}

dependencies {
    implementation(catalogue.dsl.cqrs)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.reactor.core)
    api(catalogue.spring.boot.exception.http)
    api(project(":fs-s2:file:fs-file-client"))
    implementation(libs.bundles.ktor.client)
    api(libs.spring.web)
}
