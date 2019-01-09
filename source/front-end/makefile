SHELL:=/bin/bash

project = front-end
image = ${PRIVATE_DOCKER_REGISTRY}/$(project)

install:
	# npm is required, install that first using nvm for instance. Then:

	#npm install -g typescript
	# install vscode manually for now

	#wget https://raw.githubusercontent.com/photonstorm/phaser3-docs/master/typescript/phaser.d.ts -O \
	#src/ts_definitions/phaser.d.ts

	if ! [[ `docker image ls tscz -q | wc -l` = 1 ]]; then \
		#Building Typescript build image; \
		#------------------------------------------------------------------; \
		docker build -f Dockerfile.typescript . -t tsc; \
	fi

	npm --prefix src install


build:
	

	#------------------------------------------------------------------
	# Building Typescript code
	#------------------------------------------------------------------
	docker run -u`id -u`:`id -g` -v `pwd`/src:/appsrc -w /appsrc tsc tsc

	#------------------------------------------------------------------
	# Building docker image
	#------------------------------------------------------------------
	docker build . -t $(image)

run-tsc-dev:
	docker run -u`id -u`:`id -g` -v `pwd`/src:/appsrc -w /appsrc tsc tsc -w

run-dev: ## Runs dev server on port 8080
	@echo In another window, run make run-tsc-dev
	npm --prefix src run dev


run-docker:
	docker run -v `pwd`/src:/usr/share/nginx/html:ro -p 8081:80 --name $(project) $(image)

stop-prod:
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

