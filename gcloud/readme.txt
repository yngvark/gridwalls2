docker run -it -v `pwd`/..:/gridwalls gcloud-yk bash

Deretter

gcloud auth login
gcloud container clusters get-credentials gridwalls-cluster --zone europe-west3-a --project gridwalls2

eller noe sånt