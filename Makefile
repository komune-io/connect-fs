VERSION = $(shell cat VERSION)

GATEWAY_NAME	   	:= komune-io/fs-gateway
GATEWAY_IMG	    	:= ${GATEWAY_NAME}:${VERSION}
GATEWAY_PACKAGE	   	:= fs-api:api-gateway

STORYBOOK_DOCKERFILE	:= infra/docker/storybook/Dockerfile
STORYBOOK_NAME	   	 	:= komune-io/fs-storybook
STORYBOOK_IMG	    	:= ${STORYBOOK_NAME}:${VERSION}


## Docker
docker: docker-build docker-push

docker-build: docker-fs-api-build
docker-push: docker-fs-api-push

docker-fs-api-build:
	VERSION=${VERSION} IMAGE_NAME=${GATEWAY_NAME} ./gradlew build ${GATEWAY_PACKAGE}:bootBuildImage -x test

docker-fs-api-push:
	@docker push ${GATEWAY_IMG}

## Docs
docs: docs-build docs-push

docs-build: package-storybook-build
docs-push: package-storybook-push

package-kotlin: build-libs publish-libs

package-storybook-build:
	@docker build --build-arg CI_NPM_AUTH_TOKEN=${CI_NPM_AUTH_TOKEN} -f ${STORYBOOK_DOCKERFILE} -t ${STORYBOOK_IMG} .

package-storybook-push:
	@docker push ${STORYBOOK_IMG}

## Libs
libs: package-kotlin

lint: lint-libs
build: build-libs
test: test-libs
publish: publish-libs
promote: promote-libs

lint-libs:
	echo 'No Lint'
	#./gradlew detekt

build-libs:
	./gradlew build publishToMavenLocal -x test

test-libs:
	echo 'No Tests'
#	./gradlew test

publish-libs:
	VERSION=$(VERSION) PKG_MAVEN_REPO=github ./gradlew publish --info

promote-libs:
	VERSION=$(VERSION) PKG_MAVEN_REPO=sonatype_oss ./gradlew publish


version:
	@VERSION=$$(cat VERSION); \
	echo "$$VERSION"
