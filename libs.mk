VERSION = $(shell cat VERSION)

lint:
	echo 'No Lint'
	#./gradlew detekt

build:
	./gradlew build publishToMavenLocal -x test

test:
	echo 'No Tests'
	./gradlew test

publish:
	VERSION=$(VERSION) PKG_MAVEN_REPO=github ./gradlew publish --info

promote:
	VERSION=$(VERSION) PKG_MAVEN_REPO=sonatype_oss ./gradlew publish
