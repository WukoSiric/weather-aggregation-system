#!/bin/bash
AGGREGATION_LOG="Tests/IT-4-Aggregation.log"
CLIENT_LOG="Tests/IT-4-Client.log"
CONTENT_LOG="Tests/IT-4-Content.log"

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

# Sleep to allow the servers to process requests
sleep 2

# Wait for approximately 30 seconds to ensure data expunging
echo "Waiting for data to be expunged... (30 seconds)"
sleep 30

# Perform GET requests using the GETClient and capture its output
echo "Performing GET request with single station in AggregationServer after data expunging"
make run-client1 > "$CLIENT_LOG" 2>&1

# Verify that the data has been expunged
# You can add your validation logic here, e.g., check for a specific response in the CLIENT_LOG

# Kill the AggregationServer after tests are done
kill $(pgrep -f "AggregationServer")

# Clean up compiled Java classes and generated files
make clean

# Exit with success code
exit 0
