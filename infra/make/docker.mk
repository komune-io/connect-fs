VERSION = $(shell cat VERSION)
DOCKER_REPOSITORY = ghcr.io/

GATEWAY_NAME	   	:= fs-gateway
GATEWAY_IMG	    	:= ${GATEWAY_NAME}:${VERSION}
GATEWAY_PACKAGE	   	:= fs-api:api-gateway

.PHONY: lint build test stage promote

lint:
	@echo 'No Lint'

build: docker-fs-api-build docker-fs-script-build

test:
	@echo 'No test'

stage: docker-fs-api-stage docker-fs-script-stage

promote: docker-fs-api-promote docker-fs-script-promote

docker-fs-api-build:
	VERSION=${VERSION} ./gradlew build ${GATEWAY_PACKAGE}:bootBuildImage --imageName ${GATEWAY_IMG} -x test

docker-fs-api-stage:
	@docker tag ${GATEWAY_IMG} ghcr.io/komune-io/${GATEWAY_IMG}
	@docker push ghcr.io/komune-io/${GATEWAY_IMG}

docker-fs-api-promote:
	@docker tag ${GATEWAY_IMG} docker.io/komune/${GATEWAY_IMG}
	@docker push docker.io/komune/${GATEWAY_IMG}


# fs-script docker image targets
SCRIPT_NAME := fs-script
SCRIPT_IMG := ${SCRIPT_NAME}:${VERSION}
SCRIPT_PACKAGE := fs-script:fs-script-gateway

docker-fs-script-build:
	VERSION=${VERSION} ./gradlew build ${SCRIPT_PACKAGE}:bootBuildImage --imageName ${SCRIPT_IMG} -x test

docker-fs-script-stage:
	@docker tag ${SCRIPT_IMG} ghcr.io/komune-io/${SCRIPT_IMG}
	@docker push ghcr.io/komune-io/${SCRIPT_IMG}

docker-fs-script-promote:
	@docker tag ${SCRIPT_IMG} docker.io/komune/${SCRIPT_IMG}
	@docker push docker.io/komune/${SCRIPT_IMG}
