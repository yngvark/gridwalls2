rm -rf container/app
cd source
gradle build installDist
cd ..
cp -r source/build/install/zombie container/app
