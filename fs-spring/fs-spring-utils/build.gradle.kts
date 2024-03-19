plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    id("io.komune.fixers.gradle.publish")
}

dependencies {
    implementation("io.komune.f2:f2-dsl-cqrs:${Versions.f2}")
    api("io.komune.f2:f2-spring-boot-exception-http:${Versions.f2}")
    api(project(":fs-s2:file:fs-file-client"))
    Dependencies.ktor(::implementation)
    Dependencies.Spring.frameworkWeb(::api)
}
