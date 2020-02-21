package com.collector;

import com.collector.github.GithubInfoCollectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner
{
    private static final Logger logger = LoggerFactory.getLogger(SpringBootConsoleApplication.class);

    public static void main(String[] args) throws Exception {
        logger.info("Application Started");
        SpringApplication.run(SpringBootConsoleApplication.class,args);
        logger.info("Application Ended");
    }

    @Autowired
    private GithubInfoCollectorFactory githubInfoCollectorFactory;

    @Override
    public void run(String... args) throws Exception {
        Optional<GithubInfoCollectorFactory.GithubInfoCollectorType> githubInfoCollectorType=Optional.of(GithubInfoCollectorFactory.GithubInfoCollectorType.FILE);
        String userName="aspnetboilerplate",repoName="aspnetboilerplate";
        if (args!=null && args.length==3){
            githubInfoCollectorType = GithubInfoCollectorFactory.GithubInfoCollectorType.of(args[0]);
            userName=args[1];
            repoName=args[2];
        }
        githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectStragezers(userName,repoName);
        //githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectForkers(userName,repoName);
        //githubInfoCollectorFactory.of(githubInfoCollectorType.get()).collectWatchers(userName,repoName);
    }
}
