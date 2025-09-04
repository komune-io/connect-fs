plugins {
    id("org.springframework.boot")
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
}

dependencies {
    // FS Script modules
    implementation(project(":fs-script:fs-script-core"))
    implementation(project(":fs-script:fs-script-import"))
    
    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter:${Versions.springBoot}")
    
    // F2 client for authentication
    Dependencies.Mpp.f2ClientDomain(::implementation)
    
    // Logging
    Dependencies.Logging.slf4j(::implementation)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {}
