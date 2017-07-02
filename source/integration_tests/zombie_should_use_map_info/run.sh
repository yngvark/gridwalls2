docker-compose -f rabbit.yml up &\
	(docker-compose -f test.yml up --force-recreate; echo -------- RUNNNG STUFF DOWN; docker-compose -f rabbit.yml down; (cd ../../microservices/zombie && docker-compose down); (cd ../../microservices/map_info_producer && docker-compose down)) &\
	(cd ../../microservices/zombie && ./run.sh) &\
	(cd ../../microservices/map_info_producer && ./run.sh)

echo Done.
#docker-compose -f rabbit.yml down
