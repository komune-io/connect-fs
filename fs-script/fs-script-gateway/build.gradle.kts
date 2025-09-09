plugins {
    id("org.springframework.boot")
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":fs-script:fs-script-core"))
    implementation(project(":fs-script:fs-script-import"))
    
    implementation("org.springframework.boot:spring-boot-starter:${Versions.springBoot}")
    
    Dependencies.Mpp.f2ClientDomain(::implementation)
    
    Dependencies.Logging.slf4j(::implementation)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {}

