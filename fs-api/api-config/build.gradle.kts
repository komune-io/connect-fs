plugins {
    alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
    alias(catalogue.plugins.kotlin.spring)
    alias(catalogue.plugins.kotlin.kapt)
}

dependencies {
    api(libs.f2.spring.boot.starter.function)
    api(libs.f2.spring.boot.starter.auth.tenant)

    implementation(project(":fs-s2:file:fs-file-domain"))
    api(libs.minio)
}
