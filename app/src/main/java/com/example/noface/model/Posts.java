package com.example.noface.model;

import com.google.gson.annotations.SerializedName;

import java.time.DateTimeException;
import java.util.ArrayList;

public class Posts {

    public Posts(Integer IDPost, Integer IDTopic, String IDUser, String title, String content, DateTimeException time, ArrayList<Like> likes, ArrayList<Comment> cmts) {
        this.IDPost = IDPost;
        this.IDTopic = IDTopic;
        this.IDUser = IDUser;
        Title = title;
        Content = content;
        Time = time;
        Likes = likes;
        Cmts = cmts;
    }

    public Integer getIDPost() {
        return IDPost;
    }

    public void setIDPost(Integer IDPost) {
        this.IDPost = IDPost;
    }

    public Integer getIDTopic() {
        return IDTopic;
    }

    public void setIDTopic(Integer IDTopic) {
        this.IDTopic = IDTopic;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
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

    public ArrayList<Like> getLikes() {
        return Likes;
    }

    public void setLikes(ArrayList<Like> likes) {
        Likes = likes;
    }

    public ArrayList<Comment> getCmts() {
        return Cmts;
    }

    public void setCmts(ArrayList<Comment> cmts) {
        Cmts = cmts;
    }

    private Integer IDPost;
    private Integer IDTopic;
    private String IDUser;
    private String Title;
    private String Content;
    private DateTimeException Time;

    ArrayList<Like> Likes;

    @SerializedName("Comment")
    ArrayList<Comment> Cmts;

}
