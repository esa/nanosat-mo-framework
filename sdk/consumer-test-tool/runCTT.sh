#!/bin/bash

JAR_PATTERN="target/consumer-test-tool-*-jar-with-dependencies.jar"
JAR_PATH=$(ls $JAR_PATTERN 2>/dev/null | head -n 1)

# Check if the target directory exists
if [ ! -d "target" ]; then
  echo "Error: 'target' directory does not exist. Please build the project before running this script!"
  exit 1
fi

# Check if the JAR file exists
if [ ! -f "$JAR_PATH" ]; then
  echo "Error: JAR file '$JAR_PATH' not found. Please build the project before running this script!"
  exit 1
fi

# Run the JAR file
java -jar "$JAR_PATH"

