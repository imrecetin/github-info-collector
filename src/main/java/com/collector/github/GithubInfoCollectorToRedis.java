package com.collector.github;

import org.springframework.stereotype.Service;

@Service
public class GithubInfoCollectorToRedis extends GithubInfoCollector {
    
    @Override
    public void collectStragezers(String userName, String repoName) {

    }

    @Override
    public void collectForkers(String userName, String repoName) {

    }

    @Override
    public void collectWatchers(String userName, String repoName) {

    }

    @Override
    public void collectContributors(String userName, String repoName) {

    }
}
