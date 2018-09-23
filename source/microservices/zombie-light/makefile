build-local:
	./source/gradlew -p source clean installDist

build: build-local
	docker build . -t zombie-light

run: build
	docker-compose up

ks: build
	docker tag zombie-light eu.gcr.io/gridwalls2/zombie-light
	gcloud docker -- push eu.gcr.io/gridwalls2/zombie-light

ks-run:
	helm install local/gridwalls-microservice -f helm_values.yaml --name zombie-light
