FROM ubuntu

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common START

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Command line tools START

RUN apt-get update && \
	apt-get install -y wget nano man curl

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Command line tools END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - NodeJS START

#RUN apt-get update -y && \
RUN apt-get install --no-install-recommends -y -q \
	curl python build-essential git ca-certificates
RUN mkdir /nodejs && \
	curl http://nodejs.org/dist/v0.10.36/node-v0.10.36-linux-x64.tar.gz | tar xvzf - -C /nodejs --strip-components=1
ENV PATH $PATH:/nodejs/bin

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - NodeJS END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Browserify START

#RUN sudo npm install -g browserify

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Browserify END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Visual Code START

RUN apt-get install -y sudo

RUN export uid=1000 gid=1000 && \
	mkdir -p /home/developer && \
	echo "developer:x:${uid}:${gid}:Developer,,,:/home/developer:/bin/bash" >> /etc/passwd && \
	echo "developer:x:${uid}:" >> /etc/group && \
	echo "developer ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/developer && \
	chmod 0440 /etc/sudoers.d/developer && \
	chown ${uid}:${gid} -R /home/developer

USER developer

RUN sudo apt-get install -y xterm

ENV TERM xterm

ENV VISUALCODEFILE code_1.8.1-1482158209_amd64.deb

RUN sudo apt-get install -y libnotify4 libnss3 libgtk2.0-0 libxss1 libgconf-2-4 libasound2

RUN cd /tmp && \
	wget https://az764295.vo.msecnd.net/stable/ee428b0eead68bf0fb99ab5fdc4439be227b6281/$VISUALCODEFILE -o wget.log

RUN cd /tmp && \
	sudo dpkg -i $VISUALCODEFILE

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Visual Code END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup START

RUN sudo rm -rf /var/lib/apt/lists/*

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup END
