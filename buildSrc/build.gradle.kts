plugins {
	`kotlin-dsl`
}

dependencies {
	implementation("io.komune.fixers.gradle:dependencies:0.23.0")
}

repositories {
	mavenCentral()
	mavenLocal()
	maven { url = uri("https://s01.oss.sonatype.org/service/local/repositories/releases/content") }
	maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
}
