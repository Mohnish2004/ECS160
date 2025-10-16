package com.ecs160.hw.model;

import java.util.ArrayList;
import java.util.List;

public class Repo {
    private String name;
    private String ownerLogin;
    private String htmlUrl;
    private int forksCount;
    private String language;
    private int openIssuesCount;
    private List<Repo> forks;
    private List<Commit> recentCommits;
    private List<Issue> issues;
    private int commitCount;

    public Repo(String name, String ownerLogin, String htmlUrl, int forksCount, String language, int openIssuesCount) {
        this.name = name;
        this.ownerLogin = ownerLogin;
        this.htmlUrl = htmlUrl;
        this.forksCount = forksCount;
        this.language = language;
        this.openIssuesCount = openIssuesCount;
        this.forks = new ArrayList<>();
        this.recentCommits = new ArrayList<>();
        this.issues = new ArrayList<>();
        this.commitCount = 0;
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
}
