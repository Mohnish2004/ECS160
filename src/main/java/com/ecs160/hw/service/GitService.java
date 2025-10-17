package com.ecs160.hw.service;

import com.ecs160.hw.model.Repo;
import com.ecs160.hw.util.JsonHandler;
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

    public void getRecentCommits(Repo repo) throws IOException {
        String url = String.format("%s/repos/%s/%s/commits", GITHUB_API_URL, repo.getOwnerLogin(), repo.getName());

        Request request = buildRequest(url);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response " + response);
            }
            jsonHandler.parseCommits(response.body().string(), repo);
        }
    }
}
