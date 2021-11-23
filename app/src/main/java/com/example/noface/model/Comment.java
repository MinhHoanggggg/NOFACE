package com.example.noface.model;

import java.util.ArrayList;

public class Comment {

    public Comment(int IDCmt, int IDPost, String IDUser, String content, String time, ArrayList<LikeComment> likeComment) {
        this.IDCmt = IDCmt;
        this.IDPost = IDPost;
        this.IDUser = IDUser;
        Content = content;
        Time = time;
        LikeComment = likeComment;
    }

    public int getIDCmt() {
        return IDCmt;
    }

    public void setIDCmt(int IDCmt) {
        this.IDCmt = IDCmt;
    }

    public int getIDPost() {
        return IDPost;
    }

    public void setIDPost(int IDPost) {
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

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public ArrayList<LikeComment> getLikeComment() {
        return LikeComment;
    }

    public void setLikeComment(ArrayList<LikeComment> likeComment) {
        LikeComment = likeComment;
    }

    private int IDCmt;
    private int IDPost;
    private String IDUser;
    private String Content;
    private String Time;
    private ArrayList<LikeComment> LikeComment;
}
