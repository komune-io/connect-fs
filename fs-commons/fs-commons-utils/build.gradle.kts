plugins {
    alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
}

dependencies {
    api(libs.jackson.module.kotlin)
}
