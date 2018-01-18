build:
	./source/gradlew -p source clean installDist

run:
	docker-compose build
	docker-compose up

k8s:
	docker build . -t map-info-producer
	docker tag map-info-producer eu.gcr.io/gridwalls2/map-info-producer
	gcloud docker -- push eu.gcr.io/gridwalls2/map-info-producer
