package com.ecs160.hw.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.ecs160.hw.model.Issue;
import com.ecs160.hw.model.Repo;
import redis.clients.jedis.Jedis;


public class RedisService {
    private Jedis jedis;

    public RedisService() {
        // Connect to local Redis server
        jedis = new Jedis("localhost", 6379);
    }

    public void storeRepos(List<Repo> repos, String language) {
        Integer iter = 0;
        for (Repo repo : repos) {
            iter++;
            String key = "repos:" + language + ":" + String.valueOf(iter);
            // Create a hash map to store repo details
            Map<String, String> repoMap = new HashMap<>();
            repoMap.put("name", repo.getName());
            repoMap.put("owner", repo.getOwnerLogin());
            repoMap.put("url", repo.getHtmlUrl());
            repoMap.put("language", repo.getLanguage());
            repoMap.put("stars", String.valueOf(repo.getStargazersCount()));
            repoMap.put("forks", String.valueOf(repo.getForksCount()));
            repoMap.put("openIssues", String.valueOf(repo.getOpenIssuesCount()));
            repoMap.put("commitsAfterFork", String.valueOf(repo.getCommitsAfterForkCount()));

            String issueIds = repo.getIssues().stream().map(issue -> String.valueOf(issue.getNumber())).collect(Collectors.joining(","));
            repoMap.put("issues", issueIds);
            
            jedis.hset(key, repoMap);
            storeIssues(repo.getIssues(), language);
        }
    }

    private void storeIssues(List<Issue> issues, String language) {
        for (Issue issue : issues) {
            String issueKey = "issue:" + language + ":" + issue.getNumber();
            
            Map<String, String> issueMap = new HashMap<>();
            issueMap.put("number", String.valueOf(issue.getNumber()));
            issueMap.put("title", issue.getTitle());
            issueMap.put("body", issue.getBody() != null ? issue.getBody() : "");
            issueMap.put("state", issue.getState());
            issueMap.put("createdAt", issue.getCreatedAt().toString());
            issueMap.put("updatedAt", issue.getUpdatedAt().toString());
            
            jedis.hset(issueKey, issueMap);
        }
    }

    public void close() {
        // Close the Jedis socket
        jedis.close();
    }
}