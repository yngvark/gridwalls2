# gridwalls2
Gridwalls - the game

What's more fun than learning stuff by implementing a game?

Current plan:
- Make a cool game
- Use microservices, docker and other stuff

## How to run the game

* Start a Kubernetes cluster
* Install helm
* Run RabbitMQ
  * `make -C source/microservices/rabbitmq ks ks-run`
* Run helm repo
  * `helm repo remove local`
  * `helm repo add local http://127.0.0.1:8879`
  * `make -C source/lib/helm_charts package host`
* Run map-info-producer
  * `make -C source/microservices/map_info_producer ks ks-run`
  * `make -C source/microservices/zombie-light ks ks-run`
