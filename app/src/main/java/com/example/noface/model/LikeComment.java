package com.example.noface.model;

public class LikeComment {

    public LikeComment(int ID_Like_Comment, int IDCmt, String IDUser) {
        this.ID_Like_Comment = ID_Like_Comment;
        this.IDCmt = IDCmt;
        this.IDUser = IDUser;
    }

    public int getID_Like_Comment() {
        return ID_Like_Comment;
    }

    public void setID_Like_Comment(int ID_Like_Comment) {
        this.ID_Like_Comment = ID_Like_Comment;
    }

    public int getIDCmt() {
        return IDCmt;
    }

    public void setIDCmt(int IDCmt) {
        this.IDCmt = IDCmt;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    private int ID_Like_Comment;
    private int IDCmt;
    private String IDUser;

}
