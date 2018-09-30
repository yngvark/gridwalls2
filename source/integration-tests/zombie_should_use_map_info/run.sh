#docker-compose -f test.yml kill
#docker-compose -f rabbit.yml up -d

#exit 0

#if [ $? -ne 0 ] ; then
#	echo ------------------------------------------------------------------------------------
#	echo Rabbitmq failed.
#	echo ------------------------------------------------------------------------------------
#  exit -1
#fi


#(docker-compose -f test.yml up --force-recreate --remove-orphans; \
#	echo -------- RUNNNG STUFF DOWN; \
#	docker-compose -f test.yml down; \
#	(cd ../../microservices/zombie && docker-compose down); \
#	(cd ../../microservices/map_info_producer && docker-compose down) \
#) &\
#(cd ../../microservices/zombie && ./run.sh) &\
#(cd ../../microservices/map_info_producer && ./run.sh)

docker-compose -f test.yml up --force-recreate --remove-orphans &\
(cd ../../microservices/zombie && ./run.sh) &\
(cd ../../microservices/map_info_producer && ./run.sh)

echo ------------------------------------------------------------------------------------
echo Get exit code.
echo ------------------------------------------------------------------------------------

TEST_EXIT_CODE=`docker wait zombieshouldusemapinfo_zombie_should_use_map_info_1`

echo ------------------------------------------------------------------------------------
echo Exit code: $TEST_EXIT_CODE
echo ------------------------------------------------------------------------------------

# TODO What if test never returns? Should have timeout.

echo ------------------------------------------------------------------------------------
echo Exiting zombie
echo ------------------------------------------------------------------------------------

(cd ../../microservices/zombie && docker-compose down);

echo ------------------------------------------------------------------------------------
echo Exiting map_info_producer
echo ------------------------------------------------------------------------------------

(cd ../../microservices/map_info_producer && docker-compose down)

echo ------------------------------------------------------------------------------------
echo Exiting test
echo ------------------------------------------------------------------------------------

docker-compose -f test.yml down;

echo ------------------------------------------------------------------------------------
echo Exititing with exit code $TEST_EXIT_CODE
echo ------------------------------------------------------------------------------------

exit $TEST_EXIT_CODE

#docker-compose -f rabbit.yml down
