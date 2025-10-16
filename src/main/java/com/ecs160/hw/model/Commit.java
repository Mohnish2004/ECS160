package com.ecs160.hw.model;

import java.time.ZonedDateTime;

public class Commit {
    private String sha;
    private String message;
    private ZonedDateTime date;
    private Owner author;

    public Commit(String sha, String message, ZonedDateTime date, Owner author) {
        this.sha = sha;
        this.message = message;
        this.date = date;
        this.author = author;
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
}
