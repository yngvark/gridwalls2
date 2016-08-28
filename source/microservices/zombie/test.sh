echo 1 is: $1

if [[ $1 == "-b" ]]; then
	echo --- Building ---
	rm -rf container/app
	cd source
	gradle build installDist
	cd ..
	cp -r source/build/install/zombie container/app

	echo --- Creating pipes for testing. ---
	rm -f container/app/in_pipe
	rm -f container/app/out_pipe
	mkfifo container/app/in_pipe
	mkfifo container/app/out_pipe
	exit;
fi

if [[ $1 == "-t" ]]; then
	echo --- Running with in_pipe and out_pipe ---
	./container/app/bin/zombie < container/app/in_pipe | tee container/app/out_pipe
	exit;
fi

echo --- Running normally with in_pipe ---
./container/app/bin/zombie < container/app/in_pipe

