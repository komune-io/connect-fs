plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    api(catalogue.spring.boot.starter.function)
    api(catalogue.spring.boot.starter.auth.tenant)

    implementation(project(":fs-s2:file:fs-file-domain"))
    api(libs.minio)
}
