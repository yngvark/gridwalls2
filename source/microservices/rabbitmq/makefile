build:
	docker build . -t ${GR_DOCKER_REGISTRY}/rabbitmq-gridwalls

run:
	docker-compose up

ks-build: build
	docker push ${GR_DOCKER_REGISTRY}/rabbitmq-gridwalls
	
ks-run:
	helm install k8s/helm_chart/rabbitmq --set image=${PRIVATE_DOCKER_REGISTRY}/rabbitmq-gridwalls --name rabbitmq

gcloud-push:
	docker tag rabbitmq-gridwalls ${PRIVATE_DOCKER_REGISTRY}/rabbitmq-gridwalls
	docker push ${PRIVATE_DOCKER_REGISTRY}/rabbitmq-gridwalls

gcloud-run:
	helm install k8s/helm_chart/rabbitmq --set image=${PRIVATE_DOCKER_REGISTRY}/rabbitmq-gridwalls --name rabbitmq
