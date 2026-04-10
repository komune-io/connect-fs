plugins {
	id("io.komune.fixers.gradle.kotlin.jvm")
	kotlin("plugin.spring")
	kotlin("plugin.serialization")
}

dependencies {
	api(project(":fs-s2:file:fs-file-domain"))
	api(project(":fs-infra:kb:fs-kb-client"))
	implementation(project(":fs-commons:fs-commons-utils"))

	implementation(project(":fs-api:api-config"))
	implementation(project(":fs-spring:fs-spring-utils"))

	implementation(libs.s2.spring.boot.starter.sourcing.ssm)
	implementation(libs.spring.boot.starter.webflux)
	implementation(libs.spring.boot.starter.data.redis.reactive)
	implementation(libs.lettuce.core)
	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.junit.platform.suite)
	api(libs.bundles.ktor.client)
}
