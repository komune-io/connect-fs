plugins {
	id("io.komune.fixers.gradle.kotlin.mpp")
	id("io.komune.fixers.gradle.publish")
	kotlin("plugin.serialization")
}

dependencies {
	commonMainApi("io.komune.s2:s2-automate-dsl:${Versions.s2}")

	// s2-automate-dsl used to re-export f2-dsl-function transitively (through
	// c2-ssm-chaincode-dsl). Since S2 dropped its C2 dependency it exports only
	// f2-dsl-cqrs, so F2Function has to be declared here.
	commonMainApi("io.komune.f2:f2-dsl-function:${Versions.f2}")
}
