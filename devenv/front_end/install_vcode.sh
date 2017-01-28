

#apt install --no-install-recommends -y -q \
#	curl python build-essential git ca-certificates

curl -sL https://deb.nodesource.com/setup_7.x | sudo -E bash -
	sudo apt-get install -y nodejs

npm install -g typescript

VISUALCODEFILE=code_1.8.1-1482158209_amd64.deb

#cd /tmp
#wget https://az764295.vo.msecnd.net/stable/ee428b0eead68bf0fb99ab5fdc4439be227b6281/$VISUALCODEFILE -o wget.log
#sudo dpkg -i $VISUALCODEFILE

# start with /usr/share/code/code --disable-gpu %U
