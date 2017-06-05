#!/bin/bash

function local_lib() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: lib
	echo ---------------------------------------------------------------------------------------------------

	lib/os_process_exiter/source/gradlew -p lib/os_process_exiter/source publishToMavenLocal
	lib/communicate_through_named_pipes/source/gradlew -p lib/communicate_through_named_pipes/source publishToMavenLocal
	lib/rabbitmq_helper/source/gradlew -p lib/rabbitmq_helper/source publishToMavenLocal
	lib/process_test_helper/source/gradlew -p lib/process_test_helper/source publishToMavenLocal
}

function local_ms() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building locally: microservices
	echo ---------------------------------------------------------------------------------------------------

	cd microservices
	cd netcom_forwarder && ./build.sh && cd ..
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
	cd os_process_exiter && ./publish_artifact_docker.sh && cd ..
	cd communicate_through_named_pipes && ./publish_artifact_docker.sh && cd ..
	cd rabbitmq_helper && ./publish_artifact_docker.sh && cd ..
	cd process_test_helper && ./publish_artifact_docker.sh && cd ..
	cd ..

}

function docker_ms() {
	echo ---------------------------------------------------------------------------------------------------
	echo Building in docker: ms
	echo ---------------------------------------------------------------------------------------------------

	cd microservices
	cd netcom_forwarder && ./build.sh && cd ..
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
