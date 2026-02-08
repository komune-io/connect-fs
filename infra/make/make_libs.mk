VERSION = $(shell cat VERSION)

.PHONY: lint build test stage promote

lint:
	./gradlew detekt

build:
	./gradlew build publishToMavenLocal -x test

test:
	./gradlew test

check:
	./gradlew sonar -Dsonar.token=${SONAR_TOKEN} -Dorg.gradle.parallel=true

stage:
	VERSION=$(VERSION) ./gradlew stage

promote:
	VERSION=$(VERSION) ./gradlew promote
