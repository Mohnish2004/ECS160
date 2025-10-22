package com.ecs160.hw.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import com.ecs160.hw.model.Repo;

import redis.clients.jedis.Jedis;


public class RedisService {
    private Jedis jedis;

    public RedisService() {
        // Connect to local Redis server
        jedis = new Jedis("localhost", 6379);
    }

    public void storeRepo(Repo repo) {
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
        jedis.hset("UUID: " + UUID.randomUUID().toString(), repoMap);
    }

    public void close() {
        // Close the Jedis socket
        jedis.close();
    }
}