export TILLER_NAMESPACE=project
export PRIVATE_DOCKER_REGISTRY=eu.gcr.io/vast-service-217305

echo
echo Building libs
##cd source && ./build_all.sh -ll && cd ..
cd source && echo hei && cd ..

pwd

echo
echo netcom-forwarder
make -C source/microservices/netcom-forwarder build push

echo
echo rabbitmq
make -C source/microservices/rabbitmq run

echo
echo map-info-producer
make -C source/microservices/map-info-producer run

echo
echo zombie-light
make -C source/microservices/zombie-light 
