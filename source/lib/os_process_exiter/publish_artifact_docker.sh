docker run \
	-v `pwd`/source:/app_source \
	-v localMavenRepo:/root/.m2 \
	-v gradleHome:/root/.gradle \
	-e GRADLE_OPTS=-Dorg.gradle.daemon=false \
	openjdk:8-jdk \
	/bin/bash -c "
	mkdir -p /tmp;
	echo --- Copying.
	cp -r /app_source/* /tmp
	echo --- Building.
	/tmp/gradlew -p /tmp publishToMavenLocal;
	"
