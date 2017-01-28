FROM ubuntu

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common environment variables START

# Set the locale
RUN locale-gen en_US.UTF-8  
ENV LANG en_US.UTF-8  
ENV LANGUAGE en_US:en  
ENV LC_ALL en_US.UTF-8  

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common environment variables END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Sublime START

# Taken from https://github.com/jfrazelle/dockerfiles/tree/master/sublime-text-3

RUN apt-get update && \
	apt-get -y install \
	ca-certificates \
	curl \
	tar \
	bzip2 \
	libglib2.0-0 \
	libx11-6 \
	libcairo2 \
	libpango-1.0-0 \
	libpangocairo-1.0-0 \
	libgtk2.0-0 \
	--no-install-recommends

RUN curl -sSL "https://download.sublimetext.com/sublime_text_3_build_3114_x64.tar.bz2" -o /tmp/sublime.tar.bz2 \
	&& mkdir -p /usr/src/sublime_text \
	&& tar -xjf /tmp/sublime.tar.bz2 -C /usr/src/sublime_text --strip-components 1 \
	&& rm /tmp/sublime.tar.bz2*

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Sublime END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Create home user START

RUN apt-get install -y sudo

RUN export uid=1000 gid=1000 && \
	mkdir -p /home/developer && \
	echo "developer:x:${uid}:${gid}:Developer,,,:/home/developer:/bin/bash" >> /etc/passwd && \
	echo "developer:x:${uid}:" >> /etc/group && \
	echo "developer ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/developer && \
	chmod 0440 /etc/sudoers.d/developer && \
	chown ${uid}:${gid} -R /home/developer

USER developer
ENV HOME /home/developer

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Create home user END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup START

RUN sudo rm -rf /var/lib/apt/lists/*

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup END
