kubectl create namespace project
# https://medium.com/google-cloud/helm-on-gke-cluster-quick-hands-on-guide-ecffad94b0

kubectl apply -f helm-service-account.yaml
helm init --service-account helm --tiller-namespace project
echo "export TILLER_NAMESPACE=project" >> ~/.bashrc
