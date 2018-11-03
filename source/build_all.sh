#!/bin/bash
set -e

function local_lib() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: lib
	echo ---------------------------------------------------------------------------------------------------

	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: os-process-exiter
	echo ---------------------------------------------------------------------------------------------------
	lib/os-process-exiter/source/gradlew -p lib/os-process-exiter/source publishToMavenLocal

	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: communicate-through-named_pipes
	echo ---------------------------------------------------------------------------------------------------
	lib/communicate-through-named-pipes/source/gradlew -p lib/communicate-through-named-pipes/source publishToMavenLocal

	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: rabbitmq-helper
	echo ---------------------------------------------------------------------------------------------------
	lib/rabbitmq-helper/source/gradlew -p lib/rabbitmq-helper/source publishToMavenLocal

	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: named-pipe-process-starter
	echo ---------------------------------------------------------------------------------------------------
	lib/named-pipe-process-starter/source/gradlew -p lib/named-pipe-process-starter/source publishToMavenLocal
}

function local_ms() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: microservices
	echo ---------------------------------------------------------------------------------------------------

	cd microservices
	cd netcom-forwarder && ./build.sh && cd ..
	cd zombie && ./build.sh && cd ..
	cd ..
}

function build_local() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building locally
	echo ---------------------------------------------------------------------------------------------------

	local_lib
	local_ms
}

function docker_lib() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building in docker: lib
	echo ---------------------------------------------------------------------------------------------------

	cd lib
	cd os-process-exiter && ./publish_artifact_docker.sh && cd ..
	cd communicate-through-named_pipes && ./publish_artifact_docker.sh && cd ..
	cd rabbitmq-helper && ./publish_artifact_docker.sh && cd ..
	cd named-pipe-process-starter && ./publish_artifact_docker.sh && cd ..
	cd ..

}

function docker_ms() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building in docker: ms
	echo ---------------------------------------------------------------------------------------------------

	cd microservices
	cd netcom-forwarder && ./build.sh && cd ..
	cd zombie && ./build.sh && cd ..
	cd ..

}

function build_docker() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building in docker
	echo ---------------------------------------------------------------------------------------------------

	docker_lib
	docker_ms
}

echo it is: $@

for i in "$@"
do
case $i in
	-l)
	build_local
	;;
	-d)
	build_docker
	;;
	-lib)
	local_lib
	docker_lib
	;;
	-ms)
	local_ms
	docker_ms
	;;
	-ll)
	local_lib
	;;
	*)
	build_local
	build_docker
esac
done


#if [[ "$@" == "-l" ]]; then
#	build_local
#
#if [[ "$@" == "-l" ]]; then
#
#
#
#if [[ "$@" == "-l" ]]
#then
#	build_local
#
#
#elif [[ "$@" == "-d" ]]
#then
#	build_docker
#else
#	build_local
#	build_docker
#fi
#
#
