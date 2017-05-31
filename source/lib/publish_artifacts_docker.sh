set -ex
cd communicate_through_named_pipes && ./publish_artifact_docker.sh && cd .. &&
cd os_process_exiter && ./publish_artifact_docker.sh
