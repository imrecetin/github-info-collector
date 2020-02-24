# Start with a base image containing Java runtime
FROM openjdk:8-jdk-alpine

# Add Maintainer Info
LABEL maintainer="ctn.imre@gmail.com"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/github-info-collector-1.0-SNAPSHOT.jar
ARG GITHUB_FILE=.github

ENV GITHUB_COLLECTOR_TYPE='File'
ENV GITHUB_USER_NAME='imrecetin'
ENV GITHUB_REPO_NAME='github-info-collector'

# Add the application's jar to the container
ADD ${JAR_FILE} github-info-collector.jar
COPY ${GITHUB_FILE} /root/.github

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/github-info-collector.jar","${GITHUB_COLLECTOR_TYPE} ${GITHUB_USER_NAME} ${GITHUB_REPO_NAME}"]

#https://www.digitalocean.com/community/tutorials/how-to-remove-docker-images-containers-and-volumes
#docker volume rm `docker volume ls -q -f dangling=true`
#docker rm 'docker ps -a -q'
#docker system prune -a


#mvn clean package spring-boot:repackage
#docker build --build-arg GITHUB_FILE=/home/cetinimre/.github -t github-info-collector .
#docker build --build-arg GITHUB_FILE=~/.github -t github-info-collector .
#docker build -t github-info-collector .
#docker build -t github-info-collector:forkers .
#docker build -t github-info-collector:stragezers .
#docker build -t github-info-collector:watchers .
#docker run github-info-collector
#docker run --name=github-info-collector -v /outputfiles:/outputfiles -e GITHUB_COLLECTOR_TYPE='File' -e GITHUB_USER_NAME='imrecetin' -e GITHUB_REPO_NAME='github-info-collector' github-info-collector:stragezers
#docker run --name=github-info-collector_forkers -v /outputfiles:/outputfiles -e GITHUB_COLLECTOR_TYPE='File' -e GITHUB_USER_NAME='imrecetin' -e GITHUB_REPO_NAME='github-info-collector' github-info-collector:forkers
#docker run --name=github-info-collector_watchers -v /outputfiles:/outputfiles -e GITHUB_COLLECTOR_TYPE='File' -e GITHUB_USER_NAME='imrecetin' -e GITHUB_REPO_NAME='github-info-collector' github-info-collector:watchers


#docker exec -it <mycontainer> /bin/sh
#docker run -ti --name=Container5 --volumes-from Container4 ubuntu   --shared volumes between multiple containers (volume-from)
