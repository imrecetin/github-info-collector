package com.collector.github;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class GithubInfoCollectorFactory {

    private GithubInfoCollectorToFile githubInfoCollectorToFile;
    private GithubInfoCollectorToMongo githubInfoCollectorToMongo;
    private GithubInfoCollectorToRedis githubInfoCollectorToRedis;
    private GithubInfoCollectorToNullObject githubInfoCollectorToNullObject;

    @Autowired
    public GithubInfoCollectorFactory(GithubInfoCollectorToFile githubInfoCollectorToFile,GithubInfoCollectorToMongo githubInfoCollectorToMongo,
                                      GithubInfoCollectorToRedis githubInfoCollectorToRedis,
                                      GithubInfoCollectorToNullObject githubInfoCollectorToNullObject){
        this.githubInfoCollectorToFile=githubInfoCollectorToFile;
        this.githubInfoCollectorToRedis=githubInfoCollectorToRedis;
        this.githubInfoCollectorToMongo=githubInfoCollectorToMongo;
        this.githubInfoCollectorToNullObject=githubInfoCollectorToNullObject;
    }

    public enum GithubInfoCollectorType{
        FILE("File"),REDIS("Redis"),MONGO("Mongo");
        private String type;
        GithubInfoCollectorType(String type){this.type=type;}
        public static Optional<GithubInfoCollectorType> of(String type){
            return Arrays.asList(GithubInfoCollectorType.values()).stream().filter(t->t.type.equals(type)).findFirst();
        }
    }

    public GithubInfoCollector of(GithubInfoCollectorType  githubInfoCollectorType){
        switch (githubInfoCollectorType){
            case FILE:
                return githubInfoCollectorToFile;
            case MONGO:
                return  githubInfoCollectorToMongo;
            case REDIS:
                return  githubInfoCollectorToRedis;
            default:
                return githubInfoCollectorToNullObject;
        }
    }

    public  GithubInfoCollector of(){
        return of(GithubInfoCollectorType.FILE);
    }
}
