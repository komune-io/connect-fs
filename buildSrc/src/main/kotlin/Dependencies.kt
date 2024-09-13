import io.komune.gradle.dependencies.FixersDependencies
import io.komune.gradle.dependencies.FixersPluginVersions
import io.komune.gradle.dependencies.FixersVersions
import io.komune.gradle.dependencies.Scope
import io.komune.gradle.dependencies.add

object Framework {
	val fixers = FixersPluginVersions.fixers
	val connect = "0.18.0-SNAPSHOT"
}

object PluginVersions {
	val fixers = Framework.fixers
	val d2 = Framework.fixers
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
	const val springSecurity = "6.1.3"

	const val ktor = FixersVersions.Kotlin.ktor
	const val minio = "8.5.5"
	const val reflection = "0.10.2"
}

object Repo {
	val snapshot: List<String> = listOf(
		// For fixers
		"https://oss.sonatype.org/content/repositories/snapshots",
		"https://oss.sonatype.org/service/local/repositories/releases/content",
	)
}

object Dependencies {
	object Fixers {
		fun s2SourcingSsm(scope: Scope) = scope.add(
			"io.komune.s2:s2-spring-boot-starter-sourcing-ssm:${Versions.s2}",
		)
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
