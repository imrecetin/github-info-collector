#!/bin/sh
echo "arguments: $1"
echo "arguments: $2"
echo "arguments: $3"
java -Djava.security.egd=file:/dev/./urandom -jar /github-info-collector.jar $1 $2 $3