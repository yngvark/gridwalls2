docker run -it -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v $HOME/git/devenv/intellij/developer_home:/home/developer -v $HOME/git/src:/mnt/src intellij /app/intellij/bin/idea.sh
