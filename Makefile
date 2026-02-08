VERSION = $(shell cat VERSION)

.PHONY: lint build test publish promote

## New
lint:
	@make -f infra/make/make_libs.mk lint
	@make -f infra/make/make_docker.mk lint

build:
	@make -f infra/make/make_libs.mk build
	@make -f infra/make/make_docker.mk build

test:
	@make -f infra/make/make_libs.mk test
	@make -f infra/make/make_docker.mk test

publish:
	@make -f infra/make/make_libs.mk publish
	@make -f infra/make/make_docker.mk publish

promote:
	@make -f infra/make/make_libs.mk promote
	@make -f infra/make/make_docker.mk promote

version:
	@VERSION=$$(cat VERSION); \
	echo "$$VERSION"

## DOCKER-COMPOSE DEV ENVIRONMENT
include infra/docker-compose/dev-compose.mk
