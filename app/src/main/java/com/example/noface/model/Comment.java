package com.example.noface.model;

import java.time.DateTimeException;

public class Comment {

    public Comment(Integer idcmt, Integer idpost, String iduser, String content, DateTimeException time) {
        Idcmt = idcmt;
        Idpost = idpost;
        Iduser = iduser;
        Content = content;
        Time = time;
    }

    public Integer getIdcmt() {
        return Idcmt;
    }

    public void setIdcmt(Integer idcmt) {
        Idcmt = idcmt;
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

    private Integer Idcmt;
    private Integer Idpost;
    private String Iduser;
    private String Content;
    private DateTimeException Time;
}
