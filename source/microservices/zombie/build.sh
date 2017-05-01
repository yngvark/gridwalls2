#gradle build dist -p source 
docker run --rm -v "$PWD":/project -w /project --name gradle gradle:3.4.1-jdk8 gradle build dist -p /project/source
