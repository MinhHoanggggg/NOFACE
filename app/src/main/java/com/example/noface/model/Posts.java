package com.example.noface.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Posts {
    public Posts(int IDPost, int IDTopic, String IDUser, String title, String content, String time, String imagePost, ArrayList<com.example.noface.model.Likes> likes, ArrayList<com.example.noface.model.Comment> comment) {
        this.IDPost = IDPost;
        this.IDTopic = IDTopic;
        this.IDUser = IDUser;
        Title = title;
        Content = content;
        Time = time;
        ImagePost = imagePost;
        Likes = likes;
        Comment = comment;
    }

    public Posts(int IDTopic, String IDUser, String title, String content, String time, String imagePost, ArrayList<Likes> likes, ArrayList<Comment> comment) {
        this.IDTopic = IDTopic;
        this.IDUser = IDUser;
        Title = title;
        Content = content;
        Time = time;
        ImagePost = imagePost;
        Likes = likes;
        Comment = comment;
    }

    public int getIDPost() {
        return IDPost;
    }

    public void setIDPost(int IDPost) {
        this.IDPost = IDPost;
    }

    public int getIDTopic() {
        return IDTopic;
    }

    public void setIDTopic(int IDTopic) {
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

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getImagePost() {
        return ImagePost;
    }

    public void setImagePost(String imagePost) {
        ImagePost = imagePost;
    }

    public ArrayList<Likes> getLikes() {
        return Likes;
    }

    public void setLikes(ArrayList<Likes> likes) {
        Likes = likes;
    }

    public ArrayList<Comment> getComment() {
        return Comment;
    }

    public void setComment(ArrayList<Comment> comment) {
        Comment = comment;
    }

    private int IDPost;
    private int IDTopic;
    private String IDUser;
    private String Title;
    private String Content;
    private String Time;
    private String ImagePost;

    private ArrayList<Likes> Likes;

    private ArrayList<Comment> Comment;
}
