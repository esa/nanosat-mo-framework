VERSION:=$(shell package/scripts/version.sh)
DOCKER_COMPOSE_FILE := package/docker-compose.yaml

libraries:
	rm -rf package/mof
	mkdir -p package/mof
	cp logging.properties package/mof
	find sdk/sdk-package/src/main/resources/ -type f | grep -i properties$ | xargs -i cp {} package/mof
	cp pom.xml package/mof
	cp -r core package/mof
	cp -r parent package/mof
	cp -r mission package/mof
	cp -r sdk package/mof
	cd package && docker buildx build --no-cache --platform linux/amd64,linux/arm64/v8 -f Dockerfile.Libraries -t ghcr.io/kosmoedge/nmf-libraries:latest . --push
	rm -rf package/mof	

simulator: libraries
	cd package && docker build -f Dockerfile.Simulator -t ghcr.io/kosmoedge/nmf-simulator:latest .	

supervisor: libraries
	cd package && docker buildx build --no-cache --platform linux/amd64,linux/arm64/v8 -f Dockerfile.Module -t ghcr.io/kosmoedge/supervisor:latest . --push

consumer-tool:
	cd package && docker build -f Dockerfile.ConsumerTool -t ghcr.io/kosmoedge/nmf-consumer-tool:latest .

containers: libraries simulator consumer-tool

run: containers
	mkdir -p ~/.m2/int/esa
	docker run -d --rm --name consumer-tool-temp kosmoedge/nmf-consumer-tool:latest 
	# docker cp consumer-tool-temp:/root/.m2/repository/int/esa ~/.m2/repository/int
	docker cp /mof/sdk/sdk-package/target/nmf-sdk-2.1.0-SNAPSHOT ~/.m2/repository/int
	docker stop consumer-tool-temp


space-module-%: libraries
	cd package && docker buildx build --no-cache --platform linux/amd64,linux/arm64/v8 --build-arg MODULE_PATH=sdk/examples/space/$* --build-arg VERSION=${VERSION} -f Dockerfile.Module -t ghcr.io/kosmoedge/$*:latest . --push

ground-module-%: libraries
	cd package && docker build --build-arg MODULE_PATH=sdk/examples/ground/$* --build-arg VERSION=${VERSION} -f Dockerfile.Module -t ghcr.io/kosmoedge/$*:latest .

create_docker_net:
	@ docker network inspect mo-bridge > /dev/null 2> /dev/null && : || docker network create mo-bridge

start: create_docker_net
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) up -d $(CONTAINER_NAMES)

# stop all containers or specific ones
stop:
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) stop $(CONTAINER_NAMES)

# stop all containers and remove built images
down:
	@ docker-compose -f $(DOCKER_COMPOSE_FILE) down --rmi local

# restart all or specific containers
restart: stop start