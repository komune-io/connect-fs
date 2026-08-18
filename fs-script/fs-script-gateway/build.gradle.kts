plugins {
    id("org.springframework.boot")
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":fs-script:fs-script-core"))
    implementation(project(":fs-script:fs-script-import"))

    implementation(libs.spring.boot.starter)

    implementation(catalogue.client.domain)

    implementation(libs.slf4j.api)

    testImplementation(libs.bundles.junit)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {}
