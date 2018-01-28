build:
	docker build . -t rabbitmq-gridwalls

run:
	docker-compose up

ks: build
	docker tag rabbitmq-gridwalls eu.gcr.io/gridwalls2/rabbitmq-gridwalls
	gcloud docker -- push eu.gcr.io/gridwalls2/rabbitmq-gridwalls
	
ks-run:
	helm install k8s/helm_chart/rabbitmq --set image=eu.gcr.io/gridwalls2/rabbitmq-gridwalls --name rabbitmq
