plugins {
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	maven { url = uri("https://oss.sonatype.org/content/repositories/releases") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
	implementation("io.komune.fixers.gradle:dependencies:0.17.0-SNAPSHOT")
}
