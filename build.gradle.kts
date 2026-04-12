plugins {
	alias(catalogue.plugins.f2.bom)
	alias(catalogue.plugins.kotlin.jpa) apply false
	alias(catalogue.plugins.kotlin.spring) apply false
	alias(catalogue.plugins.kotlin.serialization) apply false
	alias(catalogue.plugins.kotlin.kapt) apply false

	alias(catalogue.plugins.spring.boot) apply false

	alias(catalogue.plugins.fixers.gradle.config)
	alias(catalogue.plugins.fixers.gradle.check)
	alias(catalogue.plugins.fixers.gradle.publish)
}

fixers {
	bundle {
		id = "fs"
		group = "io.komune.fs"
		name = "FS"
		description = "File manager"
		url = "https://github.com/komune-io/connect-fs"
	}
	sonar {
		organization = "komune-io"
		projectKey = "komune-io_connect-fs"
	}
	repositories {
		sonatypeSnapshots = true
	}
}
