FROM openjdk:8-jdk-alpine

RUN set -o errexit -o nounset \
	&& echo "Installing dependencies" \
	&& apk add --no-cache \
		bash \
	&& echo "Installing dependencies" \
	&& echo "Adding arne user and group" \
	&& addgroup -S -g 1001 arne \
	&& adduser -D -S -G arne -u 1001 -s /bin/ash arne \
	&& mkdir /home/arne/.arne \
	&& chown -R arne:arne /home/arne


USER arne
VOLUME "/home/arne/.arne"
WORKDIR /home/arne



