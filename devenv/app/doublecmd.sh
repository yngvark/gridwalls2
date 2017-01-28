if [ -d "doublecmd" ]; then
	echo Running
	doublecmd/doublecmd.sh
	exit
fi

FILE=doublecmd-0.7.7.qt.x86_64.tar.xz
echo Installing $FILE

wget https://downloads.sourceforge.net/project/doublecmd/DC%20for%20Linux%2064%20bit/Double%20Commander%200.7.7%20beta/$FILE
tar xf $FILE
rm $FILE
