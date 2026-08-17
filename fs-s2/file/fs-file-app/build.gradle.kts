plugins {
	alias(catalogue.plugins.fixers.gradle.kotlin.jvm)
	alias(catalogue.plugins.kotlin.spring)
	alias(catalogue.plugins.kotlin.serialization)
}

dependencies {
	api(project(":fs-s2:file:fs-file-domain"))
	api(project(":fs-infra:kb:fs-kb-client"))
	implementation(project(":fs-commons:fs-commons-utils"))

	implementation(project(":fs-api:api-config"))
	implementation(project(":fs-spring:fs-spring-utils"))

	// S2 dropped all C2 dependencies, so the SSM sourcing starter lives in C2 now.
	// The s2.spring.sourcing.ssm package is unchanged — only the coordinate moved.
	implementation(libs.c2.ssm.s2.sourcing.spring.boot.starter)

	implementation(libs.spring.boot.starter.webflux)
	implementation(libs.bundles.spring.redis)

	testImplementation(libs.spring.boot.starter.test)
	testImplementation(libs.bundles.test.junit)

	api(libs.bundles.ktor.client)
}
