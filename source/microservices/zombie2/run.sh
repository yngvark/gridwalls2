docker run \
	-v `pwd`/container/app:/app \
	openjdk:8-jre \
	/app/bin/zombie2
