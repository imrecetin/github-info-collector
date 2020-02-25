package com.collector.github;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Service
public class GithubInfoCollectorToFile extends GithubInfoCollector {

    private static final Logger logger = LoggerFactory.getLogger(GithubInfoCollectorToFile.class);

    private static final String STRAGEZERS_FILE_PREFIX="stragezers";
    private static final String FORKERS_FILE_PREFIX="forkers";
    private static final String WATCHERS_FILE_PREFIX="watchers";
    private static final String CONTRIBUTORS_FILE_PREFIX="contributors";

    public GithubInfoCollectorToFile(){
        createDirectories();
    }

    private void createDirectories() {
        Path currentFilePath=Paths.get(Paths.get("").toAbsolutePath()+"/outputfiles");
        if (!Files.isDirectory(currentFilePath)){
            try {
                Files.createDirectories(currentFilePath);
            } catch (IOException ioException) {
               logger.error("IOException: ", ioException);
            }
        }
    }

    @Override
    public void collectStragezers(String userName, String repoName) {
        try{
            GitHub github = GitHub.connect();
            final PagedSearchIterable<GHRepository> repositories = github.searchRepositories().user(userName).repo(repoName).list();
            final PagedIterator<GHRepository> repoIterator = repositories.iterator();
            while(repoIterator.hasNext()){
                final GHRepository currentRepo = repoIterator.next();
                final List<String> existingStragezers = readExistingStragezers(currentRepo);
                final PagedIterable<GHStargazer> currentRepoStargazers = currentRepo.listStargazers2();
                final PagedIterator<GHStargazer> stargazerPagedIterator = currentRepoStargazers.iterator();
                while(stargazerPagedIterator.hasNext()){
                    final GHStargazer stargazer = stargazerPagedIterator.next();
                    final GHUser stargazerUser = stargazer.getUser();
                    if (!existingStragezers.contains(stargazerUser.getLogin())){
                        appendToStragazer(stargazerUser,currentRepo);
                    }
                }
            }
        }catch (IOException ioException){
            logger.error("IOException: ", ioException);
        }
    }

    @Override
    public void collectForkers(String userName, String repoName) {
        try{
            GitHub github = GitHub.connect();
            final PagedSearchIterable<GHRepository> repositories = github.searchRepositories().user(userName).repo(repoName).list();
            final PagedIterator<GHRepository> repoIterator = repositories.iterator();
            while(repoIterator.hasNext()){
                final GHRepository currentRepo = repoIterator.next();
                final List<String> existingForkers = readExistingForkers(currentRepo);
                final PagedIterable<GHRepository> currentRepoForks = currentRepo.listForks();
                final PagedIterator<GHRepository> forksPagedIterator = currentRepoForks.iterator();
                while(forksPagedIterator.hasNext()){
                    final GHRepository forkedRepo = forksPagedIterator.next();
                    final GHUser forkedUser = forkedRepo.getOwner();
                    if (!existingForkers.contains(forkedUser.getLogin())){
                        appendToForker(forkedUser,currentRepo);
                    }
                }
            }
        }catch (IOException ioException){
            logger.error("IOException: ", ioException);
        }
    }

    @Override
    public void collectWatchers(String userName, String repoName) {
        try{
            GitHub github = GitHub.connect();
            final PagedSearchIterable<GHRepository> repositories = github.searchRepositories().user(userName).repo(repoName).list();
            final PagedIterator<GHRepository> repoIterator = repositories.iterator();
            while(repoIterator.hasNext()){
                final GHRepository currentRepo = repoIterator.next();
                final List<String> existingWatchers = readExistingWatchers(currentRepo);
                final PagedIterable<GHUser> currentRepoWatchers = currentRepo.listSubscribers();
                final PagedIterator<GHUser> watcherPagedIterator = currentRepoWatchers.iterator();
                while(watcherPagedIterator.hasNext()){
                    final GHUser watcherUser = watcherPagedIterator.next();
                    if (!existingWatchers.contains(watcherUser.getLogin())){
                        appendToWatcher(watcherUser,currentRepo);
                    }
                }
            }
        }catch (IOException ioException){
            logger.error("IOException: ", ioException);
        }
    }

    @Override
    public void collectContributors(String userName, String repoName) {
        try{
            GitHub github = GitHub.connect();
            final PagedSearchIterable<GHRepository> repositories = github.searchRepositories().user(userName).repo(repoName).list();
            final PagedIterator<GHRepository> repoIterator = repositories.iterator();
            while(repoIterator.hasNext()){
                final GHRepository currentRepo = repoIterator.next();
                final List<String> existingContributors = readExistingContributors(currentRepo);
                final PagedIterable<GHRepository.Contributor> currentRepoContributors = currentRepo.listContributors();
                final PagedIterator<GHRepository.Contributor> contributorPagedIterator = currentRepoContributors.iterator();
                while(contributorPagedIterator.hasNext()){
                    final GHUser contributorUser = contributorPagedIterator.next();
                    if (!existingContributors.contains(contributorUser.getLogin())){
                        appendToContributor(contributorUser,currentRepo);
                    }
                }
            }
        }catch (IOException ioException) {
            logger.error("IOException: ", ioException);
        }
    }

    private List<String> readExistingContributors(GHRepository currentRepo) {
        final Path contributorsPath = Paths.get(Paths.get("").toAbsolutePath() + "/outputfiles/" + currentRepo.getName() + "_" + CONTRIBUTORS_FILE_PREFIX + ".txt");
        List<String> existingContributors=Collections.emptyList();
        if (Files.exists(contributorsPath)){
            try(Stream<String> fileLinesStream =Files.lines(contributorsPath)) {
                existingContributors = fileLinesStream.map(line -> {
                    final String[] splittedLine = line.split(";");
                    return splittedLine[0];
                }).collect(Collectors.toList());
            }catch (IOException ioException){
                logger.error("IOException: ", ioException);
            }
        }
        return existingContributors;
    }

    private List<String> readExistingForkers(final GHRepository currentRepo) {
        final Path forkersPath = Paths.get(Paths.get("").toAbsolutePath() + "/outputfiles/" + currentRepo.getName() + "_" + FORKERS_FILE_PREFIX + ".txt");
        List<String> existingForkers=Collections.emptyList();
        if (Files.exists(forkersPath)){
            try(Stream<String> fileLinesStream =Files.lines(forkersPath)) {
                existingForkers = fileLinesStream.map(line -> {
                    final String[] splittedLine = line.split(";");
                    return splittedLine[0];
                }).collect(Collectors.toList());
            }catch (IOException ioException){
                logger.error("IOException: ", ioException);
            }
        }
        return existingForkers;
    }

    private List<String> readExistingWatchers(final GHRepository currentRepo) {
        final Path watchersPath = Paths.get(Paths.get("").toAbsolutePath() + "/outputfiles/" + currentRepo.getName() + "_" + WATCHERS_FILE_PREFIX + ".txt");
        List<String> existingWatchers=Collections.emptyList();
        if (Files.exists(watchersPath)){
            try(Stream<String> fileLinesStream =Files.lines(watchersPath)) {
                existingWatchers = fileLinesStream.map(line -> {
                    final String[] splittedLine = line.split(";");
                    return splittedLine[0];
                }).collect(Collectors.toList());
            }catch (IOException ioException){
                logger.error("IOException: ", ioException);
            }
        }
        return existingWatchers;
    }

    private List<String> readExistingStragezers(final GHRepository currentRepo) {
        final Path stragezersPath = Paths.get(Paths.get("").toAbsolutePath() + "/outputfiles/" + currentRepo.getName() + "_" + STRAGEZERS_FILE_PREFIX + ".txt");
        List<String> existingStragezers=Collections.emptyList();
        if (Files.exists(stragezersPath)){
            try(Stream<String> fileLinesStream =Files.lines(stragezersPath)) {
                existingStragezers = fileLinesStream.map(line -> {
                    final String[] splittedLine = line.split(";");
                    return splittedLine[0];
                }).collect(Collectors.toList());
            }catch (IOException ioException){
                logger.error("IOException: ", ioException);
            }
        }
        return existingStragezers;
    }

    private  void appendToStragazer(final GHUser stargazerUser,final GHRepository currentRepo) throws IOException {
        final String login=stargazerUser.getLogin();
        final String email=stargazerUser.getEmail();
        final String name=stargazerUser.getName();
        final String location=stargazerUser.getLocation();
        final String company=stargazerUser.getCompany();
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Github User Name : "+login);
        System.out.println("User Email : "+email);
        System.out.println("Name Surname: "+name);
        System.out.println("User Location : "+location);
        System.out.println("User Company : "+company);
        System.out.println("--------------------------------------------------------------------");
        final List<String> fileLines= Arrays.asList(String.join(";", login, email, name, location, company));
        final Path write = Files.write(Paths.get(Paths.get("").toAbsolutePath()+"/outputfiles/"+currentRepo.getName()+"_"+STRAGEZERS_FILE_PREFIX+".txt"), fileLines ,UTF_8, APPEND,CREATE);
    }


    private void appendToContributor(final GHUser contributorUser, final GHRepository currentRepo) throws IOException {
        final String login=contributorUser.getLogin();
        final String email=contributorUser.getEmail();
        final String name=contributorUser.getName();
        final String location=contributorUser.getLocation();
        final String company=contributorUser.getCompany();
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Github User Name : "+login);
        System.out.println("User Email : "+email);
        System.out.println("Name Surname: "+name);
        System.out.println("User Location : "+location);
        System.out.println("User Company : "+company);
        System.out.println("--------------------------------------------------------------------");
        final List<String> fileLines= Arrays.asList(String.join(";", login, email, name, location, company));
        final Path write = Files.write(Paths.get(Paths.get("").toAbsolutePath()+"/outputfiles/"+currentRepo.getName()+"_"+CONTRIBUTORS_FILE_PREFIX+".txt"), fileLines ,UTF_8, APPEND,CREATE);
    }


    private  void appendToWatcher(final GHUser watcherUser,final GHRepository currentRepo) throws IOException {
        final String login=watcherUser.getLogin();
        final String email=watcherUser.getEmail();
        final String name=watcherUser.getName();
        final String location=watcherUser.getLocation();
        final String company=watcherUser.getCompany();
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Github User Name : "+login);
        System.out.println("User Email : "+email);
        System.out.println("Name Surname: "+name);
        System.out.println("User Location : "+location);
        System.out.println("User Company : "+company);
        System.out.println("--------------------------------------------------------------------");
        final List<String> fileLines= Arrays.asList(String.join(";", login, email, name, location, company));
        final Path write = Files.write(Paths.get(Paths.get("").toAbsolutePath()+"/outputfiles/"+currentRepo.getName()+"_"+WATCHERS_FILE_PREFIX+".txt"), fileLines ,UTF_8, APPEND,CREATE);
    }

    private  void appendToForker(final GHUser forkedUser,final GHRepository currentRepo) throws IOException {
        final String login=forkedUser.getLogin();
        final String email=forkedUser.getEmail();
        final String name=forkedUser.getName();
        final String location=forkedUser.getLocation();
        final String company=forkedUser.getCompany();
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Github User Name : "+login);
        System.out.println("User Email : "+email);
        System.out.println("Name Surname: "+name);
        System.out.println("User Location : "+location);
        System.out.println("User Company : "+company);
        System.out.println("--------------------------------------------------------------------");
        final List<String> fileLines= Arrays.asList(String.join(";", login, email, name, location, company));
        final Path write = Files.write(Paths.get(Paths.get("").toAbsolutePath()+"/outputfiles/"+currentRepo.getName()+"_"+FORKERS_FILE_PREFIX+".txt"), fileLines ,UTF_8, APPEND,CREATE);
    }
}
