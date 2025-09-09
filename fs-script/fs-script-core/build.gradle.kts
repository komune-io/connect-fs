plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    Dependencies.Logging.slf4j(::api)
    
    api(project(":fs-api:api-config"))
    api(project(":fs-s2:file:fs-file-app"))
    api(project(":fs-s2:file:fs-file-domain"))
    
    api("org.springframework.boot:spring-boot-starter:${Versions.springBoot}")
    api("org.springframework.boot:spring-boot-configuration-processor:${Versions.springBoot}")
    
    Dependencies.Jvm.Json.jackson(::api)
    
    api("io.komune.f2:f2-client-ktor:${Versions.f2}")

}
