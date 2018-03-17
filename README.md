# gridwalls2
Gridwalls - the game

What's more fun than learning stuff by implementing a game?

Current plan:
- Make a cool game
- Use microservices, docker and other stuff

## Installation

* Start a Kubernetes cluster

### Install Helm

From: https://github.com/kubernetes/helm/blob/master/docs/rbac.md

```
kubectl create namespace gw
kubectl create serviceaccount tiller --namespace gw
kubectl create clusterrolebinding tiller --clusterrole=cluster-admin --serviceaccount=gw:tiller
helm init --service-account tiller --tiller-namespace=gw
echo "export TILLER_NAMESPACE=gw" >> ~/.bashrc
```

### Run the game

* (Opt) Watch pods: `watch kubectl get po`
* (Opt) Watch helm releases: `watch helm list`
* Run rabbitmq
  * `make -C source/microservices/rabbitmq ks ks-run`
* Run helm repo
  * `helm repo remove local`
  * `helm repo add local http://127.0.0.1:8879`
  * `make -C source/lib/helm_charts package host`
* Run map-info-producer
  * `make -C source/microservices/map_info_producer ks ks-run`
  * (Opt) Show logs: `kubectl logs -f --namespace default $(kubectl get po -l "app=gridwalls-microservice" -l "release=map-info-producer" -o jsonpath="{.items[0].metadata.name}") netcom-forwarder`
  * `make -C source/microservices/zombie-light ks ks-run`
  * (Opt) Show logs: `kubectl logs -f --namespace default $(kubectl get po -l "app=gridwalls-microservice" -l "release=zombie-light" -o jsonpath="{.items[0].metadata.name}") netcom-forwarder`
* Run front-end
  * TODO
