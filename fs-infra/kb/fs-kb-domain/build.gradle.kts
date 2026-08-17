plugins {
    alias(catalogue.plugins.fixers.gradle.kotlin.mpp)
    alias(catalogue.plugins.kotlin.serialization)
}

dependencies {
    commonMainApi(project(":fs-s2:file:fs-file-domain"))

    // F2Function is no longer reachable transitively through s2-automate-dsl — see the
    // note in fs-s2/file/fs-file-domain/build.gradle.kts.
    commonMainApi(libs.f2.dsl.function)

    commonMainImplementation(libs.bundles.f2.client)
}
