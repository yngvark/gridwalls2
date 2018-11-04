project = netcom-forwarder
image = ${PRIVATE_DOCKER_REGISTRY}/$(project)

build:
	./source/gradlew -p source clean installDist
	docker build . -t $(image)

push:
	docker push $(image)
