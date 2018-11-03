project = netcom-forwarder
image = ${PRIVATE_DOCKER_REGISTRY}/$(project)

build:
	./source/gradlew -p source clean installDist
	docker build . -t $(project)

push:
	docker push $(image)
