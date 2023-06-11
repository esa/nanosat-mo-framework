libraries:
	rm -rf package/simulator
	mkdir -p package/simulator
	cp logging.properties package/simulator
	cp pom.xml package/simulator
	cp -r core package/simulator
	cp -r parent package/simulator
	cp -r mission package/simulator
	cp -r sdk package/simulator
	cd package && docker build -f Dockerfile.Libraries -t kosmoedge/nmf-libraries:latest .
	rm -rf package/simulator	

simulator:
	cd package && docker build -f Dockerfile.Simulator -t kosmoedge/nmf-simulator:latest .	

consumer-tool:
	cd package && docker build -f Dockerfile.ConsumerTool -t kosmoedge/nmf-consumer-tool:latest .

containers: libraries simulator consumer-tool

run: containers
	mkdir -p ~/.m2/int/esa
	docker run -d --rm --name consumer-tool-temp kosmoedge/nmf-consumer-tool:latest 
	# docker cp consumer-tool-temp:/root/.m2/repository/int/esa ~/.m2/repository/int
	docker cp /simulator/sdk/sdk-package/target/nmf-sdk-2.1.0-SNAPSHOT ~/.m2/repository/int
	docker stop consumer-tool-temp

