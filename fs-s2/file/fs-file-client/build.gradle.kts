plugins {
	alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
	alias(catalogue.plugins.fixers.gradle.publish)
	alias(catalogue.plugins.kotlin.spring)
	alias(catalogue.plugins.kotlin.serialization)
}

dependencies {
	api(project(":fs-s2:file:fs-file-domain"))
	implementation(libs.bundles.f2.client)
	implementation(libs.bundles.ktor.client)
}
