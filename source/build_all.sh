#!/bin/bash

function build_local() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building locally
	echo ---------------------------------------------------------------------------------------------------
	
	# Libraries
	lib/os_process_exiter/source/gradlew -p lib/os_process_exiter/source publishToMavenLocal
	lib/communicate_through_named_pipes/source/gradlew -p lib/communicate_through_named_pipes/source publishToMavenLocal

	# Microservices
	cd microservices
	cd netcom_forwarder && ./build.sh && cd ..
	cd zombie && ./build.sh && cd ..
	cd ..
}

function build_docker() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building in docker
	echo ---------------------------------------------------------------------------------------------------

	# Libraries
	cd lib
	cd os_process_exiter && ./publish_artifact_docker.sh && cd ..
	cd communicate_through_named_pipes && ./publish_artifact_docker.sh && cd ..
	cd ..

	# Microservices
	cd microservices
	docker-compose build netcom_forwarder
	docker-compose build zombie
	cd ..
}

if [[ "$@" == "-l" ]]
then
	build_local
	

elif [[ "$@" == "-d" ]]
then
	build_docker
else
	build_local
	build_docker
fi


