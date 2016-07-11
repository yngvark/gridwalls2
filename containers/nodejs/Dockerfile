FROM ubuntu

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common START

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Common END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Command line tools START

#RUN apt-get update && \
#	apt-get install -y nano && \
#	apt-get install -y wget && \
#	apt-get install -y man && \
#	apt-get install -y unzip && \
#	apt-get install -y make && \
#	apt-get install -y curl

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Command line tools END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - NodeJS START

RUN apt-get update && \
	apt-get install -y curl
	apt-get install -y sudo && \
	curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
	sudo apt-get install -y nodejs python node-gyp

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - NodeJS END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Browserify START

#RUN sudo npm install -g browserify

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Browserify END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup START

RUN sudo rm -rf /var/lib/apt/lists/*

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup END
