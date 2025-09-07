plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    api("io.komune.f2:f2-spring-boot-starter-function:${Versions.f2}")
    api("io.komune.f2:f2-spring-boot-starter-auth-tenant:${Versions.f2}")

    implementation(project(":fs-s2:file:fs-file-domain"))
    api("io.minio:minio:${Versions.minio}")
}
