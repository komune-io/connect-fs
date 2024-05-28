plugins {
	id("io.komune.fixers.gradle.kotlin.jvm")
	id("io.komune.fixers.gradle.publish")
	kotlin("plugin.spring")
	kotlin("plugin.serialization")
}

dependencies {
	api(project(":fs-s2:file:fs-file-domain"))
	Dependencies.Mpp.f2Client(::implementation)
	Dependencies.ktor(::implementation)
}
