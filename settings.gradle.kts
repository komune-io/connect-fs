pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
		if(System.getenv("MAVEN_LOCAL_USE") == "true") {
			mavenLocal()
		}
	}
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
		if(System.getenv("MAVEN_LOCAL_USE") == "true") {
			mavenLocal()
		}
	}
	versionCatalogs {
		// The Fixers catalogue is itself a published artifact, so its coordinate has to be
		// resolved before any catalog accessor exists. Read the version straight out of the
		// TOML — same bootstrap as fixers-s2 and fixers-c2.
		val fixersVersion = file("gradle/libs.versions.toml")
			.readLines()
			.firstNotNullOfOrNull {
				Regex("^fixers\\s*=\\s*\"([^\"]+)\"").find(it)?.groupValues?.get(1)
			} ?: error("fixers version not found in gradle/libs.versions.toml")
		create("catalogue") {
			from("io.komune.f2:f2-gradle-catalog:$fixersVersion")
		}
	}
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
