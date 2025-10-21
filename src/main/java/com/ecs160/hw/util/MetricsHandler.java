package com.ecs160.hw.util;
import com.ecs160.hw.model.Repo;
import com.ecs160.hw.model.Commit;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class MetricsHandler {
    
    public static int getTotalStars(List<Repo> repos) {
        int totalStars = 0;
        for (Repo repo : repos) {
            totalStars += repo.getStargazersCount();
        }
        return totalStars;
    }

    public static int getTotalForks(List<Repo> repos) {
        int totalForks = 0;
        for (Repo repo : repos) {
            totalForks += repo.getForksCount();
        }
        return totalForks;
    }

    public static int getTotalOpenIssues(List<Repo> repos) {
        int totalOpenIssues = 0;
        for (Repo repo : repos) {
            totalOpenIssues += repo.getOpenIssuesCount();
        }
        return totalOpenIssues;
    }

    public static List<String> getMostModifiedFilesInRecentCommits(Repo repo, int limit) {
        // get 50 most recent commits of repo
        List<Commit> commits = repo.getRecentCommits();

        // store count of each file
        Map<String, Integer> fileCounts = new HashMap<>();
        for (Commit commit : commits) {
            for (String file : commit.getModifiedFiles()) {
                fileCounts.put(file, fileCounts.getOrDefault(file, 0) + 1);
            }
        }

        // convert map to list and sort
        List<Map.Entry<String, Integer>> fileCountsList = new ArrayList<>(fileCounts.entrySet());
        fileCountsList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        // get top limit
        List<String> topModifiedList = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, fileCountsList.size()); i++) {
            topModifiedList.add(fileCountsList.get(i).getKey());
        }

        return topModifiedList;
    }

    public static int getNewCommitCountFromForkedRepos(List<Repo> repos) {
        int commitCount = 0;
        for (Repo repo: repos) {
            commitCount += repo.getCommitsAfterForkCount();
        }

        return commitCount;
    }

    public static boolean isRealCodebase(Repo repo) {
        int sourceCodeFileCount = 0;
        // loop through files
        for (String file: repo.getFiles()) {
            // count how many have source code extension 
            if (hasSourceCodeExtension(file)) {
                sourceCodeFileCount++;
            }
        }
        // calculate percentage
        double percentage = (double) sourceCodeFileCount / repo.getFiles().size() * 100;

        // if meets threshold, a real codebase
        return percentage > 30;
    }

    public static boolean hasSourceCodeExtension(String fileName) {
        return fileName.endsWith(".java") || fileName.endsWith(".c") || fileName.endsWith(".cpp") || fileName.endsWith("rs");
    }
}
