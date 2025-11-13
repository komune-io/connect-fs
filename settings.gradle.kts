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
