project = front-end
image = ${PRIVATE_DOCKER_REGISTRY}/$(project)

install:
	# sudo apt-get install nodejs-legacy
	# npm is required, install that first. Then:

	npm install -g typescript
	# install vscode manually for now

build:
	cd src/lib/npm && npm install
	tsc -p src/lib
	docker build . -t $(image)

run:
	docker run -v `pwd`/src:/usr/share/nginx/html:ro -d -p 8081:80 --name $(project) $(image)

stop:
	docker stop $(project) && docker rm $(project)

ks: build
	gcloud docker -- push $(project)
	
ks-run:
	#helm install ../../lib/helm_charts/gridwalls-microservice -f helm_config.yaml --name zombie-light
