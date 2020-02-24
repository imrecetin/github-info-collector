# Github Info Collector

You can collect user information that stars, forks and watch a repository

### Prerequisites
```
JDK 1.8 or above
Maven
```
You should install to Docker Engine to achieve this demo :
```
https://www.docker.com/get-started
```

You must get access token for own github user to get access Github API for high rate limiting (5000 requets for an hour)

### Installing

- ENV GITHUB_COLLECTOR_TYPE='File'              >> in order to save infos to file
- ENV GITHUB_USER_NAME='***********'            >> Github user name that will collect
- ENV GITHUB_REPO_NAME='***********'            >> Github user repo name that will collect

- ARG GITHUB_FILE=.github                       >> to get access Github API, you must specify a .github file including like this format
            ```login=imrecetin``` <br/>
            ```oauth=************************************``` <br/>
            
 After cloning the project on your local, you can use the steps <br/>
 
 -  ```
       mvn clean package spring-boot:repackage
       docker build --build-arg GITHUB_FILE=~/.github -t github-info-collector:forkers .
       mvn clean package spring-boot:repackage
       docker build --build-arg GITHUB_FILE=~/.github -t github-info-collector:stragezers .
       mvn clean package spring-boot:repackage
       docker build --build-arg GITHUB_FILE=~/.github -t github-info-collector:watchers .
    ```
       Before each building image stage, you should switch comment between collecting information you want 
       in SpringBootConsoleApplication.java file.
       then create and re-package executable spring console jar file.
       
        //githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectStragezers(userName,repoName);
        //githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectForkers(userName,repoName);
          githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectWatchers(userName,repoName);
        
       After that Build docker images in your local Docker Daemon host
       
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
      to store collecting info. Due to Github rate limiting, you can run your containers in every one hour,
      containers will continue where it left off.

 
