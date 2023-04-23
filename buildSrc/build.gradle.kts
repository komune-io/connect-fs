plugins {
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	maven { url = uri("https://oss.sonatype.org/content/repositories/releases") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
	implementation("city.smartb.fixers.gradle:dependencies:0.13.0")
}
