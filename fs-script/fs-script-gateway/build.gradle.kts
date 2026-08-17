plugins {
    alias(catalogue.plugins.spring.boot)
    alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
    alias(catalogue.plugins.kotlin.spring)
}

dependencies {
    implementation(project(":fs-script:fs-script-core"))
    implementation(project(":fs-script:fs-script-import"))

    implementation(libs.spring.boot.starter)

    implementation(libs.f2.client.domain)

    implementation(libs.slf4j.api)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {}
