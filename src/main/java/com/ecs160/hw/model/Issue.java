package com.ecs160.hw.model;

import java.time.ZonedDateTime;

public class Issue {
    private Integer number;
    private String title;
    private String body;
    private String state;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public Issue(Integer number, String title, String body, String state, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.number = number;
        this.title = title;
        this.body = body;
        this.state = state;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }
}
