package com.example.noface.model;

public class Like {

    public Like(String ID, String IDPost, String IDUser) {
        this.ID = ID;
        this.IDPost = IDPost;
        this.IDUser = IDUser;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIDPost() {
        return IDPost;
    }

    public void setIDPost(String IDPost) {
        this.IDPost = IDPost;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    private String ID;
    private String IDPost;
    private String IDUser;

}
