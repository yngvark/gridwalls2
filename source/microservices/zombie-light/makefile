build:
	./source/gradlew -p source clean installDist

run:
	docker-compose build
	docker-compose up

ks:
	docker build . -t zombie-light
	docker tag zombie-light eu.gcr.io/gridwalls2/zombie-light
	gcloud docker -- push eu.gcr.io/gridwalls2/zombie-light

ks-run:
	helm install ../../lib/helm_charts/gridwalls-microservice -f helm_config.yaml --name zombie-light
