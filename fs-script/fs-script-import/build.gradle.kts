plugins {
	id("io.komune.fixers.gradle.kotlin.jvm")
	id("io.komune.fixers.gradle.publish")
	kotlin("plugin.serialization")
	kotlin("kapt")
}

dependencies {
	api(project(":fs-script:fs-script-core"))
	implementation(project(":fs-commons:fs-commons-utils"))
	
	Dependencies.Jvm.Spring.autoConfigure(::implementation, ::kapt)
	Dependencies.Logging.slf4j(::implementation)
	Dependencies.Jvm.Json.jackson(::implementation)
	Dependencies.Mpp.f2ClientDomain(::implementation)

	api(project(":fs-s2:file:fs-file-app"))

	Dependencies.junit(::testImplementation)
}
