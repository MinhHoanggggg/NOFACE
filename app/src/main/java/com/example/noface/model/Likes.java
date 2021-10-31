package com.example.noface.model;

public class Likes {
    public Likes(int ID, int IDPost, String IDUser) {
        this.ID = ID;
        this.IDPost = IDPost;
        this.IDUser = IDUser;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    private int ID;
    private int IDPost;
    private String IDUser;

}
