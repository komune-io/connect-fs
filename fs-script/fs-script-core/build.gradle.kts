plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    Dependencies.Logging.slf4j(::api)
    
    // FS API and S2 dependencies
    api(project(":fs-api:api-config"))
    api(project(":fs-s2:file:fs-file-client"))
    api(project(":fs-s2:file:fs-file-domain"))
    
    // Spring Boot and configuration support
    api("org.springframework.boot:spring-boot-starter:${Versions.springBoot}")
    api("org.springframework.boot:spring-boot-configuration-processor:${Versions.springBoot}")
    
    // Jackson for JSON processing
    Dependencies.Jvm.Json.jackson(::api)
    
    // F2 client for authentication
    api("io.komune.f2:f2-client-ktor:${Versions.f2}")

}
