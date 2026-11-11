pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven {
			url = uri("https://central.sonatype.com/repository/maven-snapshots")
			content { includeGroupByRegex("""io\.komune(\..+)?""") }
			mavenContent { snapshotsOnly() }
		}
	}
}

plugins {
	id("io.komune.fixers.gradle.settings") version "0.47.0-SNAPSHOT"
}

rootProject.name = "connect-fs"

include(
	"fs-api:api-config",
	"fs-api:api-gateway",
)

include(
	"fs-commons:fs-commons-utils",
)

include(
	"fs-infra:kb:fs-kb-client",
	"fs-infra:kb:fs-kb-domain",
)

include(
	"fs-spring:fs-spring-utils",
)

include(
	"fs-s2:file:fs-file-app",
	"fs-s2:file:fs-file-client",
	"fs-s2:file:fs-file-domain",
)

include(
	"fs-script:fs-script-core",
	"fs-script:fs-script-gateway",
	"fs-script:fs-script-import"
)
