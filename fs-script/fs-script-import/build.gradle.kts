plugins {
	id("io.komune.fixers.gradle.kotlin.jvm")
	id("io.komune.fixers.gradle.publish")
	kotlin("plugin.serialization")
	kotlin("kapt")
}

dependencies {
	api(project(":fs-script:fs-script-core"))
	implementation(project(":fs-commons:fs-commons-utils"))

	implementation(libs.spring.boot.autoconfigure)
	kapt(libs.spring.boot.autoconfigure.processor)
	implementation(libs.slf4j.api)
	implementation(libs.jackson.module.kotlin)
	implementation(catalogue.client.domain)

	api(project(":fs-s2:file:fs-file-app"))

	testImplementation(libs.bundles.junit)
}
