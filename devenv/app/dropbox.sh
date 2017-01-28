mkdir -p dropbox
cd dropbox
URL=$(curl -v "https://www.dropbox.com/download?plat=lnx.x86_64" 2>&1 | tr -d '\r' | grep Location: | sed "s/^< Location: //")
TARFILE=$(echo $URL | grep -Eo [_a-zA-Z0-9\.\-]+$)

if ! [ -f $TARFILE ]; then
	echo
	echo Downloading: $URL
	echo --------------------------
	wget $URL
fi

DIRNAME=$(echo $TARFILE | sed -rn "s/([\.a-zA-Z0-9\-]*)\.tar\.gz/\1/p")
if ! [ -d $DIRNAME ]; then
	echo
	echo Extracting: $TARFILE to $DIRNAME
	echo --------------------------
	mkdir -p $DIRNAME
	tar xzf $TARFILE -C $DIRNAME
	rm $TARFILE
fi

HAS_NAUTILUS=$(dpkg-query -l nautilus-dropbox | wc -l)
if ! [ $HAS_NAUTILUS > 1 ]; then
	echo
	echo Installing nautilus-dropbox
	echo --------------------------
	sudo apt install nautilus-dropbox
fi

echo Run Dropbox with:
echo dbus-launch dropbox start
