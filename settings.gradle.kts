pluginManagement {
	// The settings plugin takes the `fixers` version of gradle/libs.versions.toml, the one it checks: one place to bump.
	val fixersVersion = settingsDir.resolve("gradle/libs.versions.toml").readLines().firstNotNullOf {
		Regex("""^fixers\s*=\s*"([^"]+)"""").find(it)?.groupValues?.get(1)
	}
	plugins {
		id("io.komune.fixers.gradle.settings") version fixersVersion
	}
	repositories {
		gradlePluginPortal()
		mavenCentral()
	}
}

plugins {
	id("io.komune.fixers.gradle.settings")
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
