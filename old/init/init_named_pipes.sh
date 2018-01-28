if ! [ -p /app_data/in ]; then
	echo Creating named pipes
	mkfifo /app_data/in
	mkfifo /app_data/out
else
	echo Creating named pipes... Alredy exists.
fi;
