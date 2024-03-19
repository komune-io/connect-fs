plugins {
    id("io.komune.fixers.gradle.kotlin.mpp")
    kotlin("plugin.serialization")
}

dependencies {
    commonMainApi(project(":fs-s2:file:fs-file-domain"))

    Dependencies.Mpp.f2Client(::commonMainImplementation)
}
