FROM debian:jessie

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

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Nginx START 
# https://github.com/nginxinc/docker-nginx/blob/11fc019b2be3ad51ba5d097b1857a099c4056213/stable/jessie/Dockerfile

# Run command "lsb_release -a" to find linux version and code name
#RUN apt-key adv --keyserver hkp://pgp.mit.edu:80 --recv-keys 573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62 && \
#	echo "deb http://nginx.org/packages/ubuntu/ wily nginx" >> /etc/apt/sources.list \
#	&& echo "deb-src http://nginx.org/packages/ubuntu/ wily nginx" >> /etc/apt/sources.list \
#	&& apt-get update \
#	&& apt-get install --no-install-recommends --no-install-suggests -y \
#						ca-certificates \
#						nginx \
#						nginx-module-xslt \
#						nginx-module-geoip \
#						nginx-module-image-filter \
#						nginx-module-perl \
#						nginx-module-njs \
#						gettext-base \
#	&& rm -rf /var/lib/apt/lists/*

ENV NGINX_VERSION 1.10.1-1~jessie

RUN apt-key adv --keyserver hkp://pgp.mit.edu:80 --recv-keys 573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62 \
	&& echo "deb http://nginx.org/packages/debian/ jessie nginx" >> /etc/apt/sources.list \
	&& apt-get update \
	&& apt-get install --no-install-recommends --no-install-suggests -y \
						ca-certificates \
						nginx=${NGINX_VERSION} \
						nginx-module-xslt \
						nginx-module-geoip \
						nginx-module-image-filter \
						nginx-module-perl \
						nginx-module-njs \
						gettext-base \
	&& rm -rf /var/lib/apt/lists/*

# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log \
	&& ln -sf /dev/stderr /var/log/nginx/error.log

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Nginx END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - NodeJS START

#RUN sudo apt-get install -y sudo
#RUN curl -sL https://deb.nodesource.com/setup_6.x | sudo -E bash -
#RUN sudo apt-get install -y nodejs python node-gyp

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - NodeJS END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Browserify START

#RUN sudo npm install -g browserify

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Browserify END

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup START

# RUN sudo rm -rf /var/lib/apt/lists/*

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Cleanup END
