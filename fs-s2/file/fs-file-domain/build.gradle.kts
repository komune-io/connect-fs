plugins {
	alias(catalogue.plugins.fixers.gradle.kotlin.mpp)
	alias(catalogue.plugins.fixers.gradle.publish)
	alias(catalogue.plugins.kotlin.serialization)
}

dependencies {
	commonMainApi(libs.s2.automate.dsl)

	// s2-automate-dsl used to re-export f2-dsl-function transitively (through
	// c2-ssm-chaincode-dsl). Since S2 dropped its C2 dependency it exports only
	// f2-dsl-cqrs, so F2Function has to be declared here.
	commonMainApi(libs.f2.dsl.function)
}
