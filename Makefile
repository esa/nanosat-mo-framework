VERSION:=$(shell package/scripts/version.sh)
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
	cd package && docker build -f Dockerfile.Libraries -t kosmoedge/nmf-libraries:latest .
	rm -rf package/mof	

simulator:
	cd package && docker build -f Dockerfile.Simulator -t kosmoedge/nmf-simulator:latest .	

consumer-tool:
	cd package && docker build -f Dockerfile.ConsumerTool -t kosmoedge/nmf-consumer-tool:latest .

containers: libraries simulator consumer-tool

run: containers
	mkdir -p ~/.m2/int/esa
	docker run -d --rm --name consumer-tool-temp kosmoedge/nmf-consumer-tool:latest 
	# docker cp consumer-tool-temp:/root/.m2/repository/int/esa ~/.m2/repository/int
	docker cp /mof/sdk/sdk-package/target/nmf-sdk-2.1.0-SNAPSHOT ~/.m2/repository/int
	docker stop consumer-tool-temp


space-module-%:
	cd package && docker build --build-arg MODULE_PATH=sdk/examples/space/$* --build-arg VERSION=${VERSION} -f Dockerfile.Module -t kosmoedge/$*:latest .


