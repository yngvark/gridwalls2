set -ex
cd communicate_through_named_pipes && ./publishToMavenLocal.sh && cd .. &&
cd os_process_exiter && ./publishToMavenLocal.sh
