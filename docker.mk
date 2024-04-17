VERSION = $(shell cat VERSION)
DOCKER_REPOSITORY = ghcr.io/

GATEWAY_NAME	   	:= ${DOCKER_REPOSITORY}komune-io/fs-gateway
GATEWAY_IMG	    	:= ${GATEWAY_NAME}:${VERSION}
GATEWAY_PACKAGE	   	:= fs-api:api-gateway

lint:
	@echo 'No Lint'

build: docker-fs-api-build

test:
	@echo 'No test'

publish: docker-fs-api-push

promote:
	@echo 'No promote'

docker-fs-api-build:
	VERSION=${VERSION} ./gradlew build ${GATEWAY_PACKAGE}:bootBuildImage --imageName ${GATEWAY_IMG} -x test

docker-fs-api-push:
	@docker push ${GATEWAY_IMG}

