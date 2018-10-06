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

run-local: build
	docker run -d -v `pwd`/src:/usr/share/nginx/html:ro -d -p 8081:80 --name $(project) $(image)

stop-local:
	docker stop $(project) && docker rm $(project)

push:
	docker push $(image)

run-helm:
	helm upgrade --install \
		$(project) \
		--set image.repository=$(image) \
		k8s/helm-chart/$(project)
#		--debug \
		#--dry-run \

run: build push run-helm

ks-run:
	#helm install ../../lib/helm_charts/gridwalls-microservice -f helm_config.yaml --name zombie-light

