package com.ecs160.hw.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;

public class Commit {
    private String sha;
    private String message;
    private ZonedDateTime date;
    private Owner author;
    private List<String> modifiedFiles;

    public Commit(String sha, String message, ZonedDateTime date, Owner author) {
        this.sha = sha;
        this.message = message;
        this.date = date;
        this.author = author;
        this.modifiedFiles = new ArrayList<String>();
    }

    public String getSha() {
        return sha;
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public Owner getAuthor() {
        return author;
    }

    public void setModifiedFiles(List<String> modifiedFiles) {
        this.modifiedFiles.addAll(modifiedFiles);
    }

    public List<String> getModifiedFiles() {
        return modifiedFiles;
    }
}
