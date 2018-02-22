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
	helm install local/gridwalls-microservice -f helm_values.yaml --name zombie-light
