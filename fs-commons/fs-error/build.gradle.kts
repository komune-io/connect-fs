plugins {
    id("io.komune.fixers.gradle.kotlin.mpp")
}

dependencies {
    commonMainApi("io.komune.f2:f2-dsl-cqrs:${Versions.f2}")
}
