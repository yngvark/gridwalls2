docker run \
	-v `pwd`:/app \
	-v localMavenRepo:/root/.m2 \
	-v gradleHome:/root/.gradle \
	-e GRADLE_OPTS=-Dorg.gradle.daemon=false \
	openjdk:8-jdk \
	/app/source/gradlew build publishToMavenLocal \
	-p /app/source
