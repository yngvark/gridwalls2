(echo rabbiting; sleep 10) &\
	((echo testing; sleep 1); echo Done!; echo Taking rabbit down)

#docker-compose -f rabbit.yml up &\
#	(docker-compose -f test.yml up; echo -------- RUNNNG STUFF DOWN; docker-compose -f rabbit.yml down) &\
#	(cd ../../microservices/zombie && ./run.sh) &\
#	(cd ../../microservices/map_info_producer && ./run.sh)

#docker-compose -f rabbit.yml down
