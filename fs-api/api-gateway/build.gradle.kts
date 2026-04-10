plugins {
    id("org.springframework.boot")
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
//    id("org.graalvm.buildtools.native")
}

dependencies {
    api(catalogue.spring.boot.starter.function.http)

    implementation(project(":fs-api:api-config"))
    implementation(project(":fs-s2:file:fs-file-app"))
    implementation(libs.reflections)

}
