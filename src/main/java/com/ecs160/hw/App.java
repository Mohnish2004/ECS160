package com.ecs160.hw;

import com.ecs160.hw.model.Commit;
import com.ecs160.hw.model.Repo;
import com.ecs160.hw.service.GitService;
import com.ecs160.hw.service.RedisService;
import com.ecs160.hw.util.JsonHandler;
import com.ecs160.hw.util.MetricsHandler;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.eclipse.jgit.api.Git;

public class App {
    
    public static void main(String[] args) {
        
        JsonHandler jsonHandler = new JsonHandler();
        GitService gitService = new GitService(jsonHandler);
        RedisService redisService = new RedisService();
        
        String[] languages = {"Java", "C", "Rust"};

        for (String language : languages) {
            System.out.println("Language: " + language);
            
            // get top 10 repos
            List<Repo> topRepos = new ArrayList<Repo>();
            try {
                topRepos = gitService.getTopRepositories(language, 10);
            } catch (Exception e) {
                System.err.println("Error getting repos: " + e.getMessage());
            }

            //store repos in Redis
            try {
                redisService.storeRepos(topRepos, language);
            } catch (Exception e) {
                System.err.println("Error storing repos in Redis: " + e.getMessage());
            }
        
            // total stars across the top 10 repos
            int totalStars = MetricsHandler.getTotalStars(topRepos);
            System.out.println("Total stars: " + totalStars);
            
            // total forks across the top 10 repos
            int totalForks = MetricsHandler.getTotalForks(topRepos);
            System.out.println("Total forks: " + totalForks);
            
            // get top 3 most modified files for each repo
            int numFiles = 3;
            System.out.println("Top-3 Most modified file per repo (as computed above)");
            for (Repo repo: topRepos) {
                System.out.println("    Repo name: " + repo.getName());
                // get 50 most recent commits for repo 
                try {
                    gitService.getRecentCommits(repo, 50);
                } catch (Exception e) {
                    System.err.println("Error getting commits: " + e.getMessage());
                }

                // get commit files for each commit 
                for (Commit commit: repo.getRecentCommits()) {
                    try {
                        gitService.getCommitFiles(repo, commit);
                    } catch (Exception e) {
                        System.err.println("Error getting commit files: " + e.getMessage());
                    }
                }

                List<String> files = MetricsHandler.getMostModifiedFilesInRecentCommits(repo, numFiles);
                for (int i = 1; i <= numFiles; i++) {
                    System.out.println("    File name" + i + ": " + files.get(i-1));
                }
            }

            // new commits in the 20 most-recent forked repos 
            // get 20 recently forked repos 
            int numForkedRepos = 20;
            List<Repo> recentlyForkedRepos = new ArrayList<Repo>();
            try {
                recentlyForkedRepos = gitService.getRecentlyForkedRepositories(language, numForkedRepos);
            } catch (Exception e) {
                System.err.println("Error getting forked repos: " + e.getMessage());
            }
            // get all new commits of for each forked repo 
            for (Repo fork: recentlyForkedRepos) {
                try {
                    gitService.getCommitsSinceFork(fork);
                } catch (Exception e) {
                    System.err.println("Error getting new commits: " + e.getMessage());
                }
            }
            // count all commits from repos 
            int newCommitCount = MetricsHandler.getNewCommitCountFromForkedRepos(recentlyForkedRepos);
            System.out.println("New commits in forked repos: " + newCommitCount);
            
            // number of open issues in top 10 repos
            int totalOpenIssues = MetricsHandler.getTotalOpenIssues(topRepos);
            System.out.println("Open issues in top-10 repos: " + totalOpenIssues);


            // find repos that are source code and not a tutorial from top 10
            for (Repo repo: topRepos) {
                // get repo contents
                try {
                    System.out.println("Getting repo contents for: " + repo.getName());
                    gitService.getRepositoryContents(repo, "");
                } catch (Exception e) {
                    System.err.println("Error getting repo contents: " + e.getMessage());
                }

                // TODO: store whether each repo is a tutorial 
                boolean isRealCodebase = MetricsHandler.isRealCodebase(repo);
                if (isRealCodebase) {
                    System.out.println("Repo: " + repo.getName() + " is real");
                } else {
                    System.out.println("Repo: " + repo.getName() + " is not real");
                }
            }

            // get most popular repo that is not a tutorial
            int maxStars = 0;
            Repo mostStarred = null;
            for (Repo repo: topRepos) {
                if (MetricsHandler.isRealCodebase(repo)) {
                    int stars = repo.getStargazersCount();
                    if (stars > maxStars) {
                        maxStars = stars;
                        mostStarred = repo;
                    }
                }
            }
            // clone most popular repo that is not a tutorial
            String localPath = "./cloned_repos/" + mostStarred.getName();
            try {
                System.out.println("Cloning repo: " + mostStarred.getName());
                Git.cloneRepository()
                    .setURI(mostStarred.getHtmlUrl() + ".git")
                    .setDirectory(new File(localPath))
                    .setDepth(1)
                    .call();
            } catch (Exception e) {
                System.err.println("Error cloning repository: " + e.getMessage());
            }
            
        }
        redisService.close();
    }
}
