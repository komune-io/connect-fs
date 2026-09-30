VERSION = $(shell cat VERSION)

.PHONY: clean lint build test stage promote

clean:
	./gradlew clean

lint:
	./gradlew detekt

build:
	./gradlew build publishToMavenLocal -x test -x jvmTest -x jsTest -x jsBrowserTest

test:
	./gradlew allTests test

#check:
	#./gradlew sonar -Dsonar.token=${FIXERS_SONAR_TOKEN} -Dorg.gradle.parallel=true

stage:
	VERSION=$(VERSION) ./gradlew stage

promote:
	VERSION=$(VERSION) ./gradlew promote
