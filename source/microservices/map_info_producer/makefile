build:
	./source/gradlew -p source clean installDist

run: build
	docker build . -t map-info-producer
	docker-compose up

ks:
	docker tag map-info-producer eu.gcr.io/gridwalls2/map-info-producer
	gcloud docker -- push eu.gcr.io/gridwalls2/map-info-producer

ks-run:
	helm install local/gridwalls-microservice -f helm_values.yaml --name map-info-producer
