#!/bin/bash

# Specify log file names and paths
AGGREGATION_LOG="Tests/aggregation_server.log"
CLIENT_LOG="Tests/get_client.log"
CONTENT_LOG="Tests/content_server.log"

# Ensure the "Tests" folder exists
mkdir -p Tests

# Start the AggregationServer in the background and capture its output
make run-server > "$AGGREGATION_LOG" 2>&1 &

# Sleep to allow the server to start
sleep 2

# Start the ContentServer to send data to the AggregationServer
echo "Performing PUT of weather1.txt with ContentServer:"
make run-content1 > "$CONTENT_LOG" 2>&1 &
sleep 1
make run-content1 >> "$CONTENT_LOG" 2>&1 &
sleep 1
make run-content1 >> "$CONTENT_LOG" 2>&1 &

# Sleep to allow the servers to process requests
sleep 2

# Perform GET requests using the GETClient and capture its output
echo "Performing GET requests:"
(make run-client1; make run-client2; make run-client3) > "$CLIENT_LOG" 2>&1

# Send all PUT requests to the ContentServer
echo "Performing PUT of weather2.txt & weather3.txt with ContentServer:"
make run-content2 >> "$CONTENT_LOG" 2>&1 &
sleep 1
make run-content3 >> "$CONTENT_LOG" 2>&1 &
sleep 2

# Perform specific GET requests using the GETClient and capture its output
echo "Performing GET requests with specified stationIDs:"
echo "***************************************************************" >> "$CLIENT_LOG" 2>&1
echo "PERFORMING GET REQUESTS WITH SPECIFIED STATIONIDS:" >> "$CLIENT_LOG" 2>&1
echo "***************************************************************" >> "$CLIENT_LOG" 2>&1
echo "[] Query for station IDS60901" >> "$CLIENT_LOG" 2>&1
make run-client1 >> "$CLIENT_LOG" 2>&1
echo "[] Query for station BNE405" >> "$CLIENT_LOG" 2>&1
make run-client2 >> "$CLIENT_LOG" 2>&1
echo "[] Query for station SYD001" >> "$CLIENT_LOG" 2>&1
make run-client3 >> "$CLIENT_LOG" 2>&1

# Perform general GET requests using the GETClient and capture its output
echo "Performing GET requests with no specified stationID:"
echo "***************************************************************" >> "$CLIENT_LOG" 2>&1
echo "PERFORMING GET REQUESTS WITH NO SPECIFIED STATIONID:" >> "$CLIENT_LOG" 2>&1
echo "***************************************************************" >> "$CLIENT_LOG" 2>&1
echo "[] Query for all stations" >> "$CLIENT_LOG" 2>&1
make run-client >> "$CLIENT_LOG" 2>&1

# Kill the AggregationServer after tests are done
kill $(pgrep -f "AggregationServer")

# Clean up compiled Java classes and generated files
make clean

# Exit with success code
exit 0
