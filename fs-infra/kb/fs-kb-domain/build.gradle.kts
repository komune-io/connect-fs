plugins {
    id("io.komune.fixers.gradle.kotlin.mpp")
    kotlin("plugin.serialization")
}

dependencies {
    commonMainApi(project(":fs-s2:file:fs-file-domain"))

    // F2Function is no longer reachable transitively through s2-automate-dsl — see the
    // note in fs-s2/file/fs-file-domain/build.gradle.kts.
    commonMainApi("io.komune.f2:f2-dsl-function:${Versions.f2}")

    Dependencies.Mpp.f2Client(::commonMainImplementation)
}
