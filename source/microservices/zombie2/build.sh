docker run \
	-v `pwd`/source:/app_source \
	-v localMavenRepo:/root/.m2 \
	-v gradleHome:/root/.gradle \
	-v gridwallsBuild:/app \
	-e GRADLE_OPTS=-Dorg.gradle.daemon=false \
	openjdk:8-jdk \
	/bin/bash -c "
	/app_source/gradlew -p /app_source build installDist;
	cp -r /app_source/build/install/app/* /app;
	"
