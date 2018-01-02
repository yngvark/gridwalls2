build:
	./source/gradlew -p source clean installDist
	docker build . -t map-info-producer
k8s: build
	docker tag map-info-producer eu.gcr.io/gridwalls2/map-info-producer
	gcloud docker -- push eu.gcr.io/gridwalls2/map-info-producer
