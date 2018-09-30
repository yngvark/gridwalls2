kubectl delete deployment -n kube-system tiller-deploy
kubectl delete deployment -n project tiller-deploy

kubectl delete svc -n kube-system tiller-deploy
kubectl delete svc -n project tiller-deploy

kubectl delete -f helm-service-account.yaml
