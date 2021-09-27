package com.example.noface.model;

import java.time.DateTimeException;

public class Posts {

    public Posts(Integer idpost, String iduser, String title, String content, DateTimeException time) {
        Idpost = idpost;
        Iduser = iduser;
        this.title = title;
        this.content = content;
        Time = time;
    }

    public Integer getIdpost() {
        return Idpost;
    }

    public void setIdpost(Integer idpost) {
        Idpost = idpost;
    }

    public String getIduser() {
        return Iduser;
    }

    public void setIduser(String iduser) {
        Iduser = iduser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTimeException getTime() {
        return Time;
    }

    public void setTime(DateTimeException time) {
        Time = time;
    }

    private Integer Idpost;
    private String Iduser;
    private String title;
    private String content;
    private DateTimeException Time;
}
