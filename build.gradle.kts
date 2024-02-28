plugins {
	kotlin("plugin.jpa") version PluginVersions.kotlin apply false
	kotlin("plugin.spring") version PluginVersions.kotlin apply false
	kotlin("plugin.serialization") version PluginVersions.kotlin apply false
	kotlin("kapt") version PluginVersions.kotlin apply false

	id("org.springframework.boot") version PluginVersions.springBoot apply false
	id("org.graalvm.buildtools.native") version PluginVersions.graalvm apply false

	id("io.komune.fixers.gradle.config") version PluginVersions.fixers
//	id("io.komune.fixers.gradle.check") version PluginVersions.fixers
	id("io.komune.fixers.gradle.d2") version PluginVersions.d2

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
		name = "Connect FileSystem"
		description = "File manager"
		url = "https://github.com/komune-io/connect-fs"
	}
}
