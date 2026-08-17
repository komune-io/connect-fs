plugins {
    alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
    alias(catalogue.plugins.kotlin.spring)
    alias(catalogue.plugins.kotlin.kapt)
}

dependencies {
    api(libs.slf4j.api)

    api(project(":fs-api:api-config"))
    api(project(":fs-s2:file:fs-file-app"))
    api(project(":fs-s2:file:fs-file-domain"))

    api(libs.spring.boot.starter)
    api(libs.spring.boot.configuration.processor)

    api(libs.jackson.module.kotlin)

    api(libs.f2.client.ktor)
}
