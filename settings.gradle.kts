pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenLocal()
		mavenCentral()
		maven { url = uri("https://s01.oss.sonatype.org/service/local/repositories/releases/content") }
		maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
	}
}

rootProject.name = "connect-fs"

include(
	"fs-api:api-config",
	"fs-api:api-gateway",
)

include(
	"fs-commons:fs-error",
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
