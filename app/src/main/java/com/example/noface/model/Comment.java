package com.example.noface.model;

import java.time.DateTimeException;

public class Comment {


    public Comment(Integer IDCmt, Integer IDPost, String IDUser, String content, DateTimeException time) {
        this.IDCmt = IDCmt;
        this.IDPost = IDPost;
        this.IDUser = IDUser;
        Content = content;
        Time = time;
    }

    public Integer getIDCmt() {
        return IDCmt;
    }

    public void setIDCmt(Integer IDCmt) {
        this.IDCmt = IDCmt;
    }

    public Integer getIDPost() {
        return IDPost;
    }

    public void setIDPost(Integer IDPost) {
        this.IDPost = IDPost;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public DateTimeException getTime() {
        return Time;
    }

    public void setTime(DateTimeException time) {
        Time = time;
    }

    private Integer IDCmt;
    private Integer IDPost;
    private String IDUser;
    private String Content;
    private DateTimeException Time;
}
