project = zombie-light
image = ${PRIVATE_DOCKER_REGISTRY}/$(project)

build:
	./source/gradlew -p source clean installDist
	docker build . -t $(image)

run-local: build
	docker build . -t $(image)
	docker-compose up

push:
	docker push $(image)

run-helm:
	helm upgrade --install \
		$(project) \
		--set app.image.repository=$(image) \
		--set netcomForwarder.image.repository=${PRIVATE_DOCKER_REGISTRY}/netcom-forwarder \
		helm/$(project)

run: build push run-helm
