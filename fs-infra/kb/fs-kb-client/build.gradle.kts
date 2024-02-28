plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    api(project(":fs-infra:kb:fs-kb-domain"))

    Dependencies.Spring.autoConfigure(::implementation, ::kapt)
    Dependencies.Mpp.f2Client(::implementation)

    Dependencies.Mpp.Ktor.Client.logging(::implementation)
    Dependencies.Mpp.Ktor.Client.auth(::implementation)
}
