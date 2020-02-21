package com.collector.github;

public abstract class GithubInfoCollector {
    public abstract void collectStragezers(String userName,String repoName);
    public abstract void collectForkers(String userName,String repoName);
    public abstract void collectWatchers(String userName,String repoName);
}
