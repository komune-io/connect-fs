plugins {
	id("io.komune.fixers.gradle.kotlin.jvm")
	id("io.komune.fixers.gradle.publish")
}

dependencies {
	api(project(":fs-script:fs-script-core"))

	implementation(libs.slf4j.api)

	testImplementation(libs.bundles.junit)
	testImplementation(libs.testcontainers)
	testImplementation(libs.testcontainers.junit.jupiter)
}
