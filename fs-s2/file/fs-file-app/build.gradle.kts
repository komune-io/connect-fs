plugins {
	id("io.komune.fixers.gradle.kotlin.jvm")
	kotlin("plugin.spring")
	kotlin("plugin.serialization")
}

dependencies {
	api(project(":fs-s2:file:fs-file-domain"))
	api(project(":fs-infra:kb:fs-kb-client"))

	implementation(project(":fs-api:api-config"))
	implementation(project(":fs-spring:fs-spring-utils"))

	Dependencies.Fixers.s2SourcingSsm(::implementation)
	Dependencies.Spring.bootWebflux(::implementation)
	Dependencies.Spring.redis(::implementation)
	Dependencies.Spring.test(::testImplementation)
	Dependencies.ktor(::api)
}
