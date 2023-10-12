#!/bin/bash

# Define the directory containing log files
LOG_DIR="./Tests"

# Find all log files in the specified directory
LOG_FILES=($(find "$LOG_DIR" -type f -name "*.log"))

# Iterate over the log files
for log_file in "${LOG_FILES[@]}"; do
    comp_file="${log_file}.comp"
    if [ -f "$comp_file" ] && [ -f "$log_file" ]; then
        checksum_comp=$(md5sum "$comp_file" | awk '{print $1}')
        checksum_log=$(md5sum "$log_file" | awk '{print $1}')
        echo "Comparing checksums for $comp_file and $log_file:"
    if [ "$checksum_comp" == "$checksum_log" ]; then
        echo "Checksums match."
    else
        echo "Checksums do not match."
    fi
    else
        echo "One or both files not found: $comp_file, $log_file"
    fi
done
