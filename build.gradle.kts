plugins {
	alias(catalogue.plugins.f2.bom)
	alias(catalogue.plugins.kotlin.jpa) apply false
	alias(catalogue.plugins.kotlin.spring) apply false
	alias(catalogue.plugins.kotlin.serialization) apply false
	alias(catalogue.plugins.kotlin.kapt) apply false

	alias(catalogue.plugins.spring.boot) apply false
	alias(libs.plugins.graalvm) apply false

	alias(catalogue.plugins.fixers.gradle.config)
	alias(catalogue.plugins.fixers.gradle.check)
	alias(catalogue.plugins.fixers.gradle.publish)
	id("io.komune.fixers.gradle.d2") version catalogue.versions.fixers.get()
}

allprojects {
	group = "io.komune.fs"
	version = System.getenv("VERSION") ?: "latest"
	repositories {
		mavenCentral()
		maven { url = uri("https://central.sonatype.com/repository/maven-snapshots") }
		if(System.getenv("MAVEN_LOCAL_USE") == "true") {
			mavenLocal()
		}
	}
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
