FROM java:8-jdk

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common START

RUN apt-get update && \
	apt-get install -y sudo nano libsecret-1-0 gnome-keyring

RUN export uid=1000 gid=1000 && \
	mkdir -p /home/developer && \
	echo "developer:x:${uid}:${gid}:Developer,,,:/home/developer:/bin/bash" >> /etc/passwd && \
	echo "developer:x:${uid}:" >> /etc/group && \
	echo "developer ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/developer && \
	chmod 0440 /etc/sudoers.d/developer && \
	sudo chown ${uid}:${gid} -R /home/developer

USER developer

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Gradle START

ENV GRADLE_VERSION 3.4

WORKDIR /tmp

RUN curl -sLO https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-all.zip && \
  sudo unzip gradle-${GRADLE_VERSION}-all.zip -d /usr/bin && \
  cd /usr/bin && \
  sudo ln -s gradle-${GRADLE_VERSION} gradle && \
  rm /tmp/gradle-${GRADLE_VERSION}-all.zip

ENV GRADLE_HOME /usr/bin/gradle
ENV PATH $PATH:$GRADLE_HOME/bin

RUN sudo mkdir /app && \
	sudo chown developer:developer /app
WORKDIR /app

RUN sudo apt-get update && \
	sudo apt-get install -y xterm

ENV TERM xterm

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Gradle END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Maven START

# ENV MAVEN_VERSION 3.3.9

# RUN mkdir -p /usr/share/maven \
#  && curl -fsSL http://apache.osuosl.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz \
#    | tar -xzC /usr/share/maven --strip-components=1 \
#  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# ENV MAVEN_HOME /usr/share/maven

# VOLUME /root/.m2

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Maven END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - IntelliJ START

ENV TARFILE ideaIU-2016.3.4.tar.gz

RUN mkdir --parents /app/intellij && \
	mkdir --parents /app/tmp

RUN mkdir --parents /app/tmp && \
	cd /app/tmp && \
	wget https://download.jetbrains.com/idea/"$TARFILE" -o wget.log

RUN tar -xf /app/tmp/"$TARFILE" --strip-components=1 -C /app/intellij

RUN rm -r /app/tmp

COPY idea-settings.jar /home/developer

COPY .bashrc /home/developer

ENV HOME /home/developer

RUN sudo chown developer:developer /home

ENTRYPOINT [ "/bin/sh", "-c" ]

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - IntelliJ END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Intellij: Lombok START

COPY lombok-plugin-0.13.16.zip /home/developer

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Intellij: Lombok END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Java SDK javadoc START

ADD jdk-8u112-docs-all.zip /home/developer

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Java SDK javadoc END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Concourse CLI START

# COPY fly /usr/local/bin

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Concourse CLI END


# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup START

RUN sudo rm -rf /var/lib/apt/lists/*

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup END
