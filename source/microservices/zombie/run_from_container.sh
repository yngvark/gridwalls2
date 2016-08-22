./build.sh && \
mkfifo container/app/pipe && \
container/app/bin/zombie < container/app/pipe | tee out.txt
