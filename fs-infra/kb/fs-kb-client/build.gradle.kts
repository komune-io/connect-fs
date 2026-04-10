plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    api(project(":fs-infra:kb:fs-kb-domain"))

    implementation(libs.spring.boot.autoconfigure)
    kapt(libs.spring.boot.autoconfigure.processor)
    implementation(catalogue.client.ktor)
    implementation(catalogue.client.domain)

    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
}
