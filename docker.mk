VERSION = $(shell cat VERSION)
DOCKER_REPOSITORY = ghcr.io/

GATEWAY_NAME	   	:= fs-gateway
GATEWAY_IMG	    	:= ${GATEWAY_NAME}:${VERSION}
GATEWAY_PACKAGE	   	:= fs-api:api-gateway

lint:
	@echo 'No Lint'

build: docker-fs-api-build

test:
	@echo 'No test'

publish: docker-fs-api-publish

promote: docker-fs-api-promote

docker-fs-api-build:
	VERSION=${VERSION} ./gradlew build ${GATEWAY_PACKAGE}:bootBuildImage --imageName ${GATEWAY_IMG} -x test

docker-fs-api-publish:
	@docker tag ${GATEWAY_IMG} ghcr.io/komune-io/${GATEWAY_IMG}
	@docker push ghcr.io/komune-io/${GATEWAY_IMG}

docker-fs-api-promote:
	@docker tag ${GATEWAY_IMG} docker.io/komune/${GATEWAY_IMG}
	@docker push docker.io/komune/${GATEWAY_IMG}
