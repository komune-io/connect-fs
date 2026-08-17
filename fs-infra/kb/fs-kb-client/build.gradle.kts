plugins {
    alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
    alias(catalogue.plugins.kotlin.spring)
    alias(catalogue.plugins.kotlin.kapt)
}

dependencies {
    api(project(":fs-infra:kb:fs-kb-domain"))

    implementation(libs.spring.boot.autoconfigure)
    kapt(libs.spring.boot.configuration.processor)

    implementation(libs.bundles.f2.client)

    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
}
