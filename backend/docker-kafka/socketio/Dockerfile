FROM java:8

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Maven START

RUN apt-get update
RUN apt-get install -y curl

ENV MAVEN_VERSION 3.3.9

RUN mkdir -p /usr/share/maven \
  && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
    | tar -xzC /usr/share/maven --strip-components=1 \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

VOLUME /root/.m2

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Maven END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Netty-Socketio START

COPY netty-socketio /app/netty-socketio
COPY netty-socketio-demo /app/netty-socketio-demo

RUN cd /app/netty-socketio && \
	mvn clean install
RUN cd /app/netty-socketio-demo/server && \
	mvn clean install

WORKDIR /app/netty-socketio-demo/server
CMD mvn exec:java

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Netty-Socketio END
