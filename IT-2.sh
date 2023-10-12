#!/bin/bash
AGGREGATION_LOG="Tests/IT-2-Aggregation.log"
CLIENT_LOG="Tests/IT-2-Client.log"
CONTENT_LOG="Tests/IT-2-Content.log"

# Ensure the "Tests" folder exists
mkdir -p Tests

# Start the AggregationServer in the background and capture its output
make run-server > "$AGGREGATION_LOG" 2>&1 &

# Sleep to allow the server to start
sleep 2

# Send all PUT requests to the ContentServer
echo "Performing PUT of weather2.txt & weather3.txt with ContentServer:"
make run-content2 > "$CONTENT_LOG" 2>&1 &
sleep 1
make run-content3 >> "$CONTENT_LOG" 2>&1 &
sleep 2

# Sleep to allow the servers to process requests
sleep 2

# Perform general GET requests using the GETClient and capture its output
echo "Performing GET requests with no specified stationID:"
make run-client >> "$CLIENT_LOG" 2>&1

# Kill the AggregationServer after tests are done
kill $(pgrep -f "AggregationServer")

# Clean up compiled Java classes and generated files
make clean

# Exit with success code
exit 0
