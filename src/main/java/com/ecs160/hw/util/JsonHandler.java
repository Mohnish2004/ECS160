package com.ecs160.hw.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ecs160.hw.model.Commit;
import com.ecs160.hw.model.Issue;
import com.ecs160.hw.model.Owner;
import com.ecs160.hw.model.Repo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {
    public JsonHandler() {
    }

    public List<Repo> parseRepositories(String json) {
        List<Repo> repositories = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray items;
        
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            items = jsonObject.has("items") ? jsonObject.getAsJsonArray("items") : new JsonArray();
        } else {
            items = jsonElement.getAsJsonArray();
        }

        for (JsonElement item : items) {
            JsonObject repoJson = item.getAsJsonObject();
            JsonObject ownerJson = repoJson.getAsJsonObject("owner");

            try {
                String language = repoJson.has("language") && !repoJson.get("language").isJsonNull() 
                    ? repoJson.get("language").getAsString() 
                    : "Unknown";
                    
                Repo repo = new Repo(
                    repoJson.get("name").getAsString(),
                    ownerJson.get("login").getAsString(),
                    repoJson.get("html_url").getAsString(),
                    repoJson.get("clone_url").getAsString(),
                    repoJson.get("forks_count").getAsInt(),
                    repoJson.get("stargazers_count").getAsInt(),
                    language,
                    repoJson.get("open_issues_count").getAsInt(),
                    repoJson.get("created_at").getAsString()
                );
                repositories.add(repo);
            } catch (Exception e) {
                System.err.println("Error parsing repository: " + e.getMessage());
            }
        }
        return repositories;
    }

    public void parseCommits(String json, Repo repo) {
        JsonArray commits = JsonParser.parseString(json).getAsJsonArray();

        for (JsonElement element : commits) {
            try {
                JsonObject commitJson = element.getAsJsonObject();
                JsonObject commitDetails = commitJson.getAsJsonObject("commit");
                
                Owner author;
                JsonElement authorElement = commitJson.get("author");
                if (authorElement != null && !authorElement.isJsonNull()) {
                    JsonObject authorJson = authorElement.getAsJsonObject();
                    author = new Owner(
                        authorJson.get("login").getAsString(),
                        authorJson.get("id").getAsLong(),
                        authorJson.get("html_url").getAsString(),
                        authorJson.get("site_admin").getAsBoolean()
                    );
                } else {
                    // Use commit author details as fallback
                    JsonObject commitAuthor = commitDetails.getAsJsonObject("author");
                    author = new Owner(
                        commitAuthor.get("name").getAsString(),
                        0L,
                        "",
                        false
                    );
                }

                Commit commit = new Commit(
                    commitJson.get("sha").getAsString(),
                    commitDetails.get("message").getAsString(),
                    ZonedDateTime.parse(commitDetails.getAsJsonObject("author").get("date").getAsString()),
                    author
                );

                repo.addCommit(commit);
            } catch (Exception e) {
                System.err.println("Error parsing commit: " + e.getMessage());
            }
        }
    }

    public void parseCommitFiles(String json, Commit commit) {
        JsonArray files = JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("files");
        List<String> modifiedFileNames = new ArrayList<String>();
        
        for (JsonElement element : files) {
            try {
                JsonObject fileJson = element.getAsJsonObject();
                String status = fileJson.get("status").getAsString();
                if ("modified".equals(status)) {
                    String fileName = fileJson.get("filename").getAsString();
                    modifiedFileNames.add(fileName);
                }
            } catch (Exception e) {
                System.err.println("Error parsing commit file: " + e.getMessage());
            }
        }
        commit.setModifiedFiles(modifiedFileNames);
    }

    public void parseCommitCount(String json, Repo repo) {
        JsonArray commits = JsonParser.parseString(json).getAsJsonArray();

        repo.setCommitAfterForkCount(commits.size());
    }

    public void parseRepoTree(String json, Repo repo) {
        JsonObject treeData = JsonParser.parseString(json).getAsJsonObject();
        JsonArray tree = treeData.getAsJsonArray("tree");

        for (JsonElement element : tree) {
            JsonObject item = element.getAsJsonObject();
            String type = item.get("type").getAsString();
            
            // Only add files 
            if ("blob".equals(type)) {
                String path = item.get("path").getAsString();
                repo.addFile(path);
            }
        }
    }

    public void parseIssues(String json, Repo repo) {
        JsonArray issues = JsonParser.parseString(json).getAsJsonArray();
        
        for (JsonElement element : issues) {
            try {
                JsonObject issueJson = element.getAsJsonObject();
                if (issueJson.has("pull_request")) {
                    continue;
                }
                Integer issueNumber = issueJson.get("number").getAsInt();
                String title = issueJson.get("title").getAsString();
                String state = issueJson.get("state").getAsString();
                String body = issueJson.has("body") && !issueJson.get("body").isJsonNull() ? issueJson.get("body").getAsString() : "";
                ZonedDateTime createdAt = ZonedDateTime.parse(issueJson.get("created_at").getAsString());
                ZonedDateTime updatedAt = ZonedDateTime.parse(issueJson.get("updated_at").getAsString());
                
                Issue issue = new Issue(issueNumber, title, body, state, createdAt, updatedAt);
                repo.addIssue(issue);
            } catch (Exception e) {
                System.err.println("Error parsing issue: " + e.getMessage());
            }
        }
    }
}
