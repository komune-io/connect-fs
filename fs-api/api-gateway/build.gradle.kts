plugins {
    id("org.springframework.boot")
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
//    id("org.graalvm.buildtools.native")
}

dependencies {
    api("io.komune.f2:f2-spring-boot-starter-function-http:${Versions.f2}")

    implementation("io.komune.c2:ssm-tx-config-spring-boot-starter:${Versions.c2}")
    implementation(project(":fs-api:api-config"))
    implementation(project(":fs-s2:file:fs-file-app"))
    implementation("org.reflections:reflections:${Versions.reflection}")

}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
    imageName.set("${System.getenv("IMAGE_NAME")}:${this.project.version}")
}
