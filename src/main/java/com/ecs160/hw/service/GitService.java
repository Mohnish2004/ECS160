package com.ecs160.hw.service;

import com.ecs160.hw.model.Commit;
import com.ecs160.hw.model.Repo;
import com.ecs160.hw.util.JsonHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;

public class GitService {
    private static final String GITHUB_API_URL = "https://api.github.com";
    private final OkHttpClient client;
    private final JsonHandler jsonHandler;
    private final String token;

    public GitService(JsonHandler jsonHandler) {
        this.client = new OkHttpClient();
        this.jsonHandler = jsonHandler;
        this.token = System.getenv("GITHUB_TOKEN") != null ? System.getenv("GITHUB_TOKEN") : "";
    }

    private Request buildRequest(String url) {
        Request.Builder requestBuilder = new Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github.v3+json");
            
        if (!token.isEmpty()) {
            requestBuilder.header("Authorization", "token " + token);
        }
        
        return requestBuilder.build();
    }

    public List<Repo> getTopRepositories(String language, int limit) throws IOException {
        String query = String.format("language:%s", language);
        String url = String.format("%s/search/repositories?q=%s&sort=stars&order=desc&per_page=%d",
                GITHUB_API_URL, query, limit);

        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            return jsonHandler.parseRepositories(response.body().string());
        }
    }

    public List<Repo> getRecentlyForkedRepositories(String language, int limit) throws IOException {
        String query = "language: " + language + " fork:true";
        String url = String.format("%s/search/repositories?q=%s&sort=updated&order=desc&per_page=%d",
                GITHUB_API_URL, query, limit);

        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            return jsonHandler.parseRepositories(response.body().string());
        }
    } 

    public List<Repo> getRepositoryForks(String owner, String repo) throws IOException {
        String url = String.format("%s/repos/%s/%s/forks?sort=newest", GITHUB_API_URL, owner, repo);

        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            return jsonHandler.parseRepositories(response.body().string());
        }
    }

    public void getRecentCommits(Repo repo, int limit) throws IOException {
        String url = String.format("%s/repos/%s/%s/commits?per_page=%d", GITHUB_API_URL, repo.getOwnerLogin(), repo.getName(), limit);

        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            jsonHandler.parseCommits(response.body().string(), repo);
        }
    }

    public void getCommitFiles(Repo repo, Commit commit) throws IOException {
        String url = String.format("%s/repos/%s/%s/commits/%s", GITHUB_API_URL, repo.getOwnerLogin(), repo.getName(), commit.getSha());

        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            jsonHandler.parseCommitFiles(response.body().string(), commit);
        }
    }

    public void getCommitsSinceFork(Repo repo) throws IOException {
        String url = String.format("%s/repos/%s/%s/commits?since=%s&per_page=100", GITHUB_API_URL, repo.getOwnerLogin(), repo.getName(), repo.getCreatedAt());

        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 409) {
                // if 409 (conflict), repo most likely has no commits yet
                // set commit count for this fork repo to 0

                repo.setCommitAfterForkCount(0);
            }
            else if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            } else {
                String body = response.body().string();
                jsonHandler.parseCommitCount(body, repo);
            }
        }
    }

    public void getRepositoryContents(Repo repo) throws IOException {
    // Get the default branch
    String url = String.format("%s/repos/%s/%s", GITHUB_API_URL, repo.getOwnerLogin(), repo.getName());
    
    Request repoRequest = buildRequest(url);
    String branch;
    
    try (Response response = client.newCall(repoRequest).execute()) {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response " + response);;
        }
        JsonObject repoData = JsonParser.parseString(response.body().string()).getAsJsonObject();
        branch = repoData.get("default_branch").getAsString();
    }
    
    // Get the tree 
    String treeUrl = String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", GITHUB_API_URL, repo.getOwnerLogin(), repo.getName(), branch);

    Request request = buildRequest(treeUrl);

    try (Response response = client.newCall(request).execute()) {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected response " + response); 
        }
        String body = response.body().string();
        jsonHandler.parseRepoTree(body, repo);
    }
}
}
