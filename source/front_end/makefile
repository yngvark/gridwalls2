build:
	docker build . -t eu.gcr.io/gridwalls2/front_end

run:
	docker run -v `pwd`/html:/usr/share/nginx/html:ro -d -p 8081:80 eu.gcr.io/gridwalls2/front_end

ks: build
	gcloud docker -- push eu.gcr.io/gridwalls2/front_end
	
ks-run:
	#helm install ../../lib/helm_charts/gridwalls-microservice -f helm_config.yaml --name zombie-light
