package com.ecs160.hw.model;

import java.util.ArrayList;
import java.util.List;

public class Repo {
    private String name;
    private String ownerLogin;
    private String htmlUrl;
    private String cloneUrl;
    private int forksCount;
    private int stargazersCount;
    private String language;
    private int openIssuesCount;
    private List<Repo> forks;
    private List<Commit> recentCommits;
    private List<Issue> issues;
    private int commitCount;
    private String createdAt;
    private int commitsAfterForkCount;
    private List<String> files;

    public Repo(String name, String ownerLogin, String htmlUrl, String cloneUrl, int forksCount, int stargazersCount, String language, int openIssuesCount, String createdAt) {
        this.name = name;
        this.ownerLogin = ownerLogin;
        this.htmlUrl = htmlUrl;
        this.cloneUrl = cloneUrl;
        this.forksCount = forksCount;
        this.stargazersCount = stargazersCount;
        this.language = language;
        this.openIssuesCount = openIssuesCount;
        this.forks = new ArrayList<>();
        this.recentCommits = new ArrayList<>();
        this.issues = new ArrayList<>();
        this.commitCount = 0;
        this.createdAt = createdAt;
        this.files = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public int getForksCount() {
        return forksCount;
    }

    public String getLanguage() {
        return language;
    }

    public int getOpenIssuesCount() {
        return openIssuesCount;
    }

    public List<Repo> getForks() {
        return forks;
    }

    public void addFork(Repo fork) {
        this.forks.add(fork);
    }

    public List<Commit> getRecentCommits() {
        return recentCommits;
    }

    public void addCommit(Commit commit) {
        this.recentCommits.add(commit);
        this.commitCount++;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void addIssue(Issue issue) {
        this.issues.add(issue);
    }

    public int getCommitCount() {
        return commitCount;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCommitAfterForkCount(int commitCount) {
        this.commitsAfterForkCount = commitCount;
    }

    public int getCommitsAfterForkCount() {
        return commitsAfterForkCount;
    }

    public List<String> getFiles() {
        return files;
    }

    public void addFile(String file) {
        this.files.add(file);
    }
}
