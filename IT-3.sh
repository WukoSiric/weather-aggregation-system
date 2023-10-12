#!/bin/bash
AGGREGATION_LOG="Tests/IT-3-Aggregation.log"
CLIENT_LOG="Tests/IT-3-Client.log"
CONTENT_LOG="Tests/IT-3-Content.log"

# Ensure the "Tests" folder exists
mkdir -p Tests

# Start the AggregationServer in the background and capture its output
make run-server > "$AGGREGATION_LOG" 2>&1 &

# Sleep to allow the server to start
sleep 2

# Put data into the AggregationServer
echo "Performing PUTS with ContentServer:"
make run-content1 > "$CONTENT_LOG" 2>&1
make run-content2 >> "$CONTENT_LOG" 2>&1
make run-content3 >> "$CONTENT_LOG" 2>&1
sleep 1


# Perform specific GET requests using the GETClient and capture its output
echo "Performing GET requests with specified stationIDs:"
make run-client1 > "$CLIENT_LOG" 2>&1
make run-client2 >> "$CLIENT_LOG" 2>&1
make run-client3 >> "$CLIENT_LOG" 2>&1

# Kill the AggregationServer after tests are done
kill $(pgrep -f "AggregationServer")

# Clean up compiled Java classes and generated files
make clean

# Exit with success code
exit 0
