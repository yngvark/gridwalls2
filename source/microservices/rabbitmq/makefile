project = rabbitmq-custom
image = ${PRIVATE_DOCKER_REGISTRY}/$(project)

build:
	docker build . -t $(image)

run-local: build
	docker build . -t $(image)
	docker-compose up

push:
	docker push $(image)

run-helm:
	helm upgrade --install \
		$(project) \
		--set image=${PRIVATE_DOCKER_REGISTRY}/$(project) \
		helm/$(project)

run: build push run-helm

get-credentials:
	echo Username      : user
	echo Password      : `kubectl get secret --namespace project rabbitmq-rabbitmq -o jsonpath="{.data.rabbitmq-password}" | base64 --decode`
