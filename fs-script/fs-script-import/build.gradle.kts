plugins {
	alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
	alias(catalogue.plugins.fixers.gradle.publish)
	alias(catalogue.plugins.kotlin.serialization)
	alias(catalogue.plugins.kotlin.kapt)
}

dependencies {
	api(project(":fs-script:fs-script-core"))
	implementation(project(":fs-commons:fs-commons-utils"))

	implementation(libs.spring.boot.autoconfigure)
	kapt(libs.spring.boot.configuration.processor)

	implementation(libs.slf4j.api)
	implementation(libs.jackson.module.kotlin)
	implementation(libs.f2.client.domain)

	api(project(":fs-s2:file:fs-file-app"))

	testImplementation(libs.bundles.test.junit)
}
