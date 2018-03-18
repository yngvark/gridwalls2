build:
	docker build . -t ${GR_DOCKER_REGISTRY}rabbitmq-gridwalls

run:
	docker-compose up

ks-build: build
	docker push ${GR_DOCKER_REGISTRY}rabbitmq-gridwalls
	
ks-run:
	helm install k8s/helm_chart/rabbitmq --set image=${GR_DOCKER_REGISTRY}rabbitmq-gridwalls --name rabbitmq

gcloud-push:
	docker tag rabbitmq-gridwalls eu.gcr.io/gridwalls2/rabbitmq-gridwalls
	gcloud docker -- push eu.gcr.io/gridwalls2/rabbitmq-gridwalls

gcloud-run:
	helm install k8s/helm_chart/rabbitmq --set image=eu.gcr.io/gridwalls2/rabbitmq-gridwalls --name rabbitmq
