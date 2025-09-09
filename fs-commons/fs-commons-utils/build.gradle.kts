plugins {
    id("io.komune.fixers.gradle.kotlin.jvm")
}

dependencies {
    Dependencies.Jvm.Json.jackson(::api)
}