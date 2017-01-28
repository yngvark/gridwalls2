mkdir -p sublime
cd sublime
URL=$(curl https://www.sublimetext.com/3 2>&1 | grep -Eo https://download.sublimetext.com/sublime_text_3_build_[0-9]+_x64.tar.bz2)
TARFILE=$(echo $URL | grep -Eo [_a-zA-Z0-9\.\-]+$)

echo Does file exist?: $TARFILE
if ! [ -f $TARFILE ]; then
	echo
	echo Downloading: $URL
	echo --------------------------
	wget $URL
fi

DIRNAME=$(echo $TARFILE | sed -rn "s/([\.a-zA-Z0-9\-]*)\.tar\.bz2/\1/p")
echo Does dir exist? $DIRNAME
if ! [ -d $DIRNAME ]; then
	echo
	echo Extracting: $TARFILE to $DIRNAME
	echo --------------------------
	mkdir -p $DIRNAME
	tar xf $TARFILE -C $DIRNAME
fi

if [ -d $DIRNAME ]; then
	$DIRNAME/sublime_text_3/sublime_text
fi
