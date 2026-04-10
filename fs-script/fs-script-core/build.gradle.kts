plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    api(libs.slf4j.api)

    api(project(":fs-api:api-config"))
    api(project(":fs-s2:file:fs-file-app"))
    api(project(":fs-s2:file:fs-file-domain"))

    api(libs.spring.boot.starter)
    api(libs.spring.boot.configuration.processor)

    api(libs.jackson.module.kotlin)

    api(catalogue.client.ktor)

}
