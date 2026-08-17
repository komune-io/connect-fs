import io.komune.fixers.gradle.dependencies.FixersDependencies
import io.komune.fixers.gradle.dependencies.FixersPluginVersions
import io.komune.fixers.gradle.dependencies.FixersVersions
import io.komune.fixers.gradle.dependencies.Scope
import io.komune.fixers.gradle.dependencies.add
import java.net.URI
import org.gradle.api.artifacts.dsl.RepositoryHandler

object Framework {
	val fixers = FixersPluginVersions.fixers
}

object PluginVersions {
	val fixers = Framework.fixers
	// The d2 Gradle plugin left the Fixers version line: fixers-d2 tags reach 0.39.0 but
	// io.komune.fixers.gradle.d2 was last published to Maven Central at 0.28.21. Pinning
	// it to Framework.fixers stopped resolving once Fixers moved past 0.28.x.
	val d2 = "0.28.21"
	const val kotlin = FixersPluginVersions.kotlin
	const val springBoot = FixersPluginVersions.springBoot
	const val graalvm = FixersPluginVersions.graalvm
}

object Versions {
	val f2 = Framework.fixers
	val s2 = Framework.fixers
	val c2 = Framework.fixers
	const val springBoot = PluginVersions.springBoot
	const val springFramework = FixersVersions.Spring.framework

	const val ktor = FixersVersions.Kotlin.ktor
	const val minio = "8.5.5"
	const val reflection = "0.10.2"
}

fun RepositoryHandler.defaultRepo() {
	mavenCentral()
	maven { url = URI("https://central.sonatype.com/repository/maven-snapshots") }
	if(System.getenv("MAVEN_LOCAL_USE") == "true") {
		mavenLocal()
	}
}

object Dependencies {
	object Jvm {
		object Spring {
			fun test(scope: Scope) = FixersDependencies.Jvm.Test.junit(scope).add(
				"org.springframework.boot:spring-boot-starter-test:${Versions.springBoot}",
			)

			fun autoConfigure(scope: Scope, ksp: Scope) = FixersDependencies.Jvm.Spring.autoConfigure(scope, ksp)
		}

		object F2 {
			fun f2Function(scope: Scope) = scope.add(
				"io.komune.f2:f2-spring-boot-starter-function:${Versions.f2}"
			)
		}
		object Json {
			fun jackson(scope: Scope) = FixersDependencies.Jvm.Json.jackson(scope)
		}
	}

	object Fixers {
		// S2 dropped all C2 dependencies, so the SSM sourcing starter moved to C2. The
		// s2.spring.sourcing.ssm package is unchanged — only the coordinate moved.
		fun s2SourcingSsm(scope: Scope) = scope.add(
			"io.komune.c2:ssm-s2-sourcing-spring-boot-starter:${Versions.c2}",
		)
	}


	object Logging {
		fun slf4j(scope: Scope) = FixersDependencies.Jvm.Logging.slf4j(scope)
	}

	object Spring {
		fun bootWebflux(scope: Scope) = scope.add(
			"org.springframework.boot:spring-boot-starter-webflux:${Versions.springBoot}"
		)

		fun frameworkWeb(scope: Scope) = scope.add(
			"org.springframework:spring-web:${Versions.springFramework}"
		)
		fun redis(scope: Scope) = scope.add(
			"org.springframework.boot:spring-boot-starter-data-redis-reactive:${Versions.springBoot}",
			"io.lettuce:lettuce-core:6.1.6.RELEASE"
		)

		fun autoConfigure(scope: Scope, ksp: Scope)
				= FixersDependencies.Jvm.Spring.autoConfigure(scope, ksp)

		fun test(scope: Scope) = scope.add(
			"org.springframework.boot:spring-boot-starter-test:${Versions.springBoot}",
		).also {
			junit(scope)
		}
	}

	object Mpp {
		fun f2(scope: Scope) = scope.add(
			"io.komune.f2:f2-dsl-function:${Versions.f2}",
			"io.komune.f2:f2-dsl-cqrs:${Versions.f2}"
		)

		fun f2Client(scope: Scope) = scope.add(
			"io.komune.f2:f2-client-ktor:${Versions.f2}",
			"io.komune.f2:f2-client-domain:${Versions.f2}",
		)
		fun f2ClientDomain(scope: Scope) = scope.add(
			"io.komune.f2:f2-client-domain:${Versions.f2}",
		)

		object Ktor {
			object Client {
				fun logging(scope: Scope) = scope.add(
					"io.ktor:ktor-client-logging:${Versions.ktor}",
				)
				fun auth(scope: Scope) = scope.add(
					"io.ktor:ktor-client-auth:${Versions.ktor}",
				)
			}
		}
	}
	fun junit(scope: Scope) = FixersDependencies.Jvm.Test.junit(scope)
	fun cucumber(scope: Scope) = FixersDependencies.Jvm.Test.cucumber(scope)

	fun ktor(scope: Scope) = scope.add(
		"io.ktor:ktor-client-core:${Versions.ktor}",
		"io.ktor:ktor-client-content-negotiation:${Versions.ktor}",
		"io.ktor:ktor-client-cio:${Versions.ktor}",
		"io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}",
	)
}
