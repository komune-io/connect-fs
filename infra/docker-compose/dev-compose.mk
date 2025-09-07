DOCKER_COMPOSE_PATH = infra/docker-compose
MAKE_OPTS =
.PHONY: $(DOCKER_COMPOSE_FILE) $(ACTIONS)
ACTIONS = up down logs log pull stop kill deploy remove help
DEFAULT_ENV = $(DOCKER_COMPOSE_PATH)/.env_dev

dev-envsubst:
	mkdir -p $(DOCKER_COMPOSE_PATH)/build/config;
	$(call copy_config,$(DOCKER_COMPOSE_PATH)/config)
	@if [ "$(CURRENT_CONTEXT)" != "default" ] && [ "$(CURRENT_CONTEXT)" != "desktop-linux" ]; then \
		$(call copy_config,$(DOCKER_COMPOSE_PATH)/config_$(CURRENT_CONTEXT)); \
	fi
	$(call copy_config,$(DOCKER_COMPOSE_PATH)/.env_dev)
	@if [ "$(CURRENT_CONTEXT)" != "default" ] && [ "$(CURRENT_CONTEXT)" != "desktop-linux" ]; then \
		$(call copy_config,$(DOCKER_COMPOSE_PATH)/.env_dev_$(CURRENT_CONTEXT),.env_dev); \
	fi
	find $(DOCKER_COMPOSE_PATH)/build -type f -exec bash -c 'envsubst < "{}" > "{}.tmp" && mv "{}.tmp" "{}"' \;

init:
	$(eval ACTION := $(filter $(ACTIONS),$(MAKECMDGOALS)))
	$(eval SERVICE := $(filter $(DOCKER_COMPOSE_INIT_FILE),$(MAKECMDGOALS)))
	$(MAKE) $(MAKE_OPTS) --no-print-directory exec-common SERVICE=$(SERVICE) SERVICES_ALL="$(DOCKER_COMPOSE_INIT_FILE)"

dev:
	$(eval ACTION := $(filter $(ACTIONS),$(MAKECMDGOALS)))
	$(eval SERVICE := $(filter $(DOCKER_COMPOSE_DEV_FILE),$(MAKECMDGOALS)))
	$(MAKE) $(MAKE_OPTS) --no-print-directory exec-common ACTION=$(ACTION) SERVICE=$(SERVICE) SERVICES_ALL="$(DOCKER_COMPOSE_DEV_FILE)"

exec-common:
	@if [ "$(ACTION)" = "up" ]; then \
  		if ! docker network ls | grep -q $(DOCKER_NETWORK); then \
  			docker network create $(DOCKER_NETWORK); \
  		fi; \
		$(MAKE) $(MAKE_OPTS) --no-print-directory dev-envsubst; \
	elif [ "$(ACTION)" = "down" ]; then \
		$(MAKE) $(MAKE_OPTS) --no-print-directory dev-envsubst-clean; \
	elif [ "$(ACTION)" = "kill" ]; then \
		$(MAKE) $(MAKE_OPTS) --no-print-directory kill-containers; \
	elif [ "$(ACTION)" = "deploy" ]; then \
	  	if ! docker network ls | grep -q $(DOCKER_NETWORK); then \
			docker network create --driver overlay --scope swarm $(DOCKER_NETWORK); \
		fi; \
		$(MAKE) $(MAKE_OPTS) --no-print-directory dev-envsubst; \
	elif [ "$(ACTION)" = "remove" ]; then \
		$(MAKE) $(MAKE_OPTS) --no-print-directory dev-envsubst-clean; \
	elif [ "$(ACTION)" = "help" ]; then \
		$(MAKE) $(MAKE_OPTS) --no-print-directory dev-help SERVICES_ALL="$(SERVICES_ALL)"; \
	fi
	@if [ "$(ACTION)" != "kill" ] && [ "$(ACTION)" != "help" ]; then \
		if [ "$(SERVICE)" = "" ]; then \
			for service in $(SERVICES_ALL); do \
				$(MAKE) $(MAKE_OPTS) --no-print-directory dev-service-action ACTION=$(ACTION) SERVICE=$$service; \
			done; \
		else \
			$(MAKE) $(MAKE_OPTS) --no-print-directory dev-service-action ACTION=$(ACTION) SERVICE=$(SERVICE); \
		fi; \
	fi

kill-containers:
	@if [ -n "$$(docker ps -q)" ]; then \
		CONTAINERS=$$(docker ps --format '{{.Names}}' | tr '\n' ',' | sed 's/,$$//; s/,/, /g'); \
		echo "Killing running containers: $$CONTAINERS"; \
		docker kill $$(docker ps -q); \
	else \
		echo "No running containers to kill."; \
	fi; \
	if [ -n "$$(docker ps -a -q)" ]; then \
		ALL_CONTAINERS=$$(docker ps -a --format '{{.Names}}' | tr '\n' ',' | sed 's/,$$//; s/,/, /g'); \
		echo "Removing containers: $$ALL_CONTAINERS"; \
		docker rm $$(docker ps -a -q); \
	else \
		echo "No containers to remove."; \
	fi; \
	if [ -n "$$(docker service ls -q)" ]; then \
		ALL_SERVICES=$$(docker service ls --format '{{.Name}}' | tr '\n' ',' | sed 's/,$$//; s/,/, /g'); \
		echo "Removing services: $$ALL_SERVICES"; \
		docker service rm $$(docker service ls -q); \
	else \
		echo "No services to remove."; \
	fi; \
	if [ -n "$$(docker network ls -q)" ]; then \
		ALL_NETWORKS=$$(docker network ls --filter type=custom --format '{{.Name}}' | tr '\n' ',' | sed 's/,$$//; s/,/, /g'); \
		echo "Removing networks: $$ALL_NETWORKS"; \
		docker network prune -f; \
	else \
		echo "No custom networks to remove."; \
	fi


dev-service-action:
	@if [ "$(ACTION)" = "up" ]; then \
		docker compose --env-file $(DOCKER_COMPOSE_ENV) -f $(DOCKER_COMPOSE_PATH)/docker-compose-$(SERVICE).yml  up -d; \
	elif [ "$(ACTION)" = "stop" ]; then \
		docker compose --env-file $(DOCKER_COMPOSE_ENV) -f $(DOCKER_COMPOSE_PATH)/docker-compose-$(SERVICE).yml stop; \
	elif [ "$(ACTION)" = "down" ]; then \
		docker compose --env-file $(DOCKER_COMPOSE_ENV) -f $(DOCKER_COMPOSE_PATH)/docker-compose-$(SERVICE).yml down -v; \
	elif [ "$(ACTION)" = "logs" ]; then \
		docker compose --env-file $(DOCKER_COMPOSE_ENV) -f $(DOCKER_COMPOSE_PATH)/docker-compose-$(SERVICE).yml logs -f; \
	elif [ "$(ACTION)" = "log" ]; then \
		docker compose --env-file $(DOCKER_COMPOSE_ENV) -f $(DOCKER_COMPOSE_PATH)/docker-compose-$(SERVICE).yml logs; \
	elif [ "$(ACTION)" = "pull" ]; then \
		docker compose --env-file $(DOCKER_COMPOSE_ENV) -f $(DOCKER_COMPOSE_PATH)/docker-compose-$(SERVICE).yml pull; \
	elif [ "$(ACTION)" = "deploy" ]; then \
		docker stack deploy --detach=true --compose-file $(DOCKER_COMPOSE_PATH)/docker-compose-$(SERVICE).yml $(DOCKER_NETWORK); \
	elif [ "$(ACTION)" = "remove" ]; then \
		docker stack rm $(DOCKER_NETWORK); \
	else \
		echo 'No valid action: $(ACTION).'; \
		echo 'Available actions are:'; \
		echo '  up    - Start the service.'; \
		echo '  down  - Stop the service.'; \
		echo '  deploy  - Deploy the service stack.'; \
		echo '  remove  - Remove the service stack.'; \
		echo '  logs    - Show logs for the service stack.'; \
		echo '  log     - Follow logs for the service stack.'; \
	fi

dev-envsubst-clean:
	rm -fr $(DOCKER_COMPOSE_PATH)/build

dev-help:
	@echo 'To operate on all services [$(SERVICES_ALL)]:'
	@echo '  make dev up    - Start all services.'
	@echo '  make dev down  - Stop all services.'
	@echo '  make dev deploy    - Deploy all service stacks.'
	@echo '  make dev remove    - Remove all service stacks.'
	@echo '  make dev logs      - Show logs for all services.'
	@echo ''
	@echo 'To operate on a specific service, use: make dev [service] [action]'
	@echo ''
	@echo 'Possible combinations are:'
	@$(foreach service,$(SERVICES_ALL), \
		echo ' Docker Compose file $(DOCKER_COMPOSE_PATH)/docker-compose-$(service).yml:'; \
		$(foreach action,$(ACTIONS), \
			echo '  make dev $(service) $(action)'; \
		) \
	)


# Dynamically determine DOCKER_COMPOSE_ENV based on Docker context with logging
$(eval CURRENT_CONTEXT := $(shell docker context inspect --format '{{ .Name }}' 2>/dev/null || echo "default"))
$(if $(filter default desktop-linux,$(CURRENT_CONTEXT)), \
    $(eval DOCKER_COMPOSE_ENV := $(DEFAULT_ENV)), \
    $(eval DOCKER_COMPOSE_ENV := $(DOCKER_COMPOSE_PATH)/.env_dev_$(CURRENT_CONTEXT)))
$(info Current Docker context: $(CURRENT_CONTEXT))
$(info Selected environment file: $(DOCKER_COMPOSE_ENV))

# Include the determined environment file if it exists
ifeq (,$(wildcard $(DOCKER_COMPOSE_ENV)))
$(warning Warning: $(DOCKER_COMPOSE_ENV) does not exist. Using default environment $(DEFAULT_ENV).)
include $(DEFAULT_ENV)
else
include $(DOCKER_COMPOSE_ENV)
endif

export

define copy_config
	if [ -d "$(1)" ]; then \
		cp -r "$(1)"/* "$(DOCKER_COMPOSE_PATH)/build/config/"; \
	elif [ -f "$(1)" ]; then \
		if [ -n "$(2)" ]; then \
			cp "$(1)" "$(DOCKER_COMPOSE_PATH)/build/config/$(2)"; \
		else \
			cp "$(1)" "$(DOCKER_COMPOSE_PATH)/build/config/"; \
		fi; \
	else \
		echo "File or directory '$(1)' does not exist or is empty"; \
	fi
endef



$(DOCKER_COMPOSE_FILE):
	@:

$(DOCKER_COMPOSE_INIT_FILE):
	@:

$(ACTIONS):
	@:
