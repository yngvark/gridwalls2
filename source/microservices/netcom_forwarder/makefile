build:
	./source/gradlew -p source clean installDist
	docker build . -t netcom-forwarder
k8s: build
	docker tag netcom-forwarder eu.gcr.io/gridwalls2/netcom-forwarder
	gcloud docker -- push eu.gcr.io/gridwalls2/netcom-forwarder
