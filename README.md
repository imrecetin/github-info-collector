# Github Info Collector

You can collect user information that stars, forks and watches a repositories. 

### Prerequisites
```
JDK 1.8 or above
Maven
```
You must install Docker Engine to use this demo:

```
https://www.docker.com/get-started
```

To access the Github API for high rate limiting, you need to get your own github user access token (5000 requests for an hour)

### Installing

- ENV GITHUB_COLLECTOR_TYPE='File'              >> to save the information in a file
- ENV GITHUB_USER_NAME='***********'            >> Github user name to collect
- ENV GITHUB_REPO_NAME='***********'            >> Github user repo name to collect
- ARG GITHUB_FILE=.github                       >> to access the Github API, you must specify a .github file containing this format:

            ```login=imrecetin``` <br/>
            ```oauth=************************************``` <br/>
            
After cloning the project in your local, you can follow the steps below: <br/>
 
 -  ```
       mvn clean package spring-boot:repackage
       docker build --build-arg GITHUB_FILE=~/.github -t github-info-collector:forkers .
       mvn clean package spring-boot:repackage
       docker build --build-arg GITHUB_FILE=~/.github -t github-info-collector:stragezers .
       mvn clean package spring-boot:repackage
       docker build --build-arg GITHUB_FILE=~/.github -t github-info-collector:watchers .
    ```
       Before building each Docker image stage, you can collect the information you want by changing between the comment lines in the SpringBootConsoleApplication.java file.
       Then create and re-package to get executable spring console jar file.
       
        //githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectStragezers(userName,repoName);
        //githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectForkers(userName,repoName);
          githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectWatchers(userName,repoName);
        
       Following that, you can build Docker images in your local Docker Daemon host.
       
 -  ``` 
        docker run --name=github-info-collector -v /outputfiles:/outputfiles \
                   -e GITHUB_COLLECTOR_TYPE='File' -e GITHUB_USER_NAME='imrecetin' \ 
                   -e GITHUB_REPO_NAME='github-info-collector' github-info-collector:stragezers
        docker run --name=github-info-collector_forkers -v /outputfiles:/outputfiles \
                   -e GITHUB_COLLECTOR_TYPE='File' -e GITHUB_USER_NAME='imrecetin' \
                   -e GITHUB_REPO_NAME='github-info-collector' github-info-collector:forkers
        docker run --name=github-info-collector_watchers -v /outputfiles:/outputfiles \
                   -e GITHUB_COLLECTOR_TYPE='File' -e GITHUB_USER_NAME='imrecetin' \
                   -e GITHUB_REPO_NAME='github-info-collector' github-info-collector:watchers
     ```
        
      If you select Collector_Type='File', bind mound a volume with flag -v (-v /outputfiles:/outputfiles)
      to store collecting info. Due to Github rate limiting, you can run your containers once an hour, the containers will continue where they left off.

 
