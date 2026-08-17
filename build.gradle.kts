plugins {
	alias(catalogue.plugins.kotlin.jpa) apply false
	alias(catalogue.plugins.kotlin.spring) apply false
	alias(catalogue.plugins.kotlin.serialization) apply false
	alias(catalogue.plugins.kotlin.kapt) apply false

	alias(catalogue.plugins.spring.boot) apply false

	alias(catalogue.plugins.f2.bom)
	alias(catalogue.plugins.fixers.gradle.config)
	alias(catalogue.plugins.fixers.gradle.check)
	alias(libs.plugins.fixers.gradle.d2)

	// Declared here so the plugin jar lands on the build classpath exactly once;
	// subprojects then alias them without re-resolving a version. Same shape as
	// fixers-s2 / fixers-c2.
	alias(catalogue.plugins.fixers.gradle.kotlin.jvm) apply false
	alias(catalogue.plugins.fixers.gradle.kotlin.mpp) apply false
	alias(catalogue.plugins.fixers.gradle.publish) apply false
}

allprojects {
	group = "io.komune.fs"
	version = System.getenv("VERSION") ?: "latest"
}

fixers {
	d2 {
		outputDirectory = file("storybook/d2/")
	}
	bundle {
		id = "fs"
		name = "FS"
		description = "File manager"
		url = "https://github.com/komune-io/connect-fs"
	}
	sonar {
		organization = "komune-io"
		projectKey = "komune-io_connect-fs"
	}
}
