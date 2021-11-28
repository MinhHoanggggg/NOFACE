package com.example.noface.model;

public class Friend {
    private int ID;
    private String IDUser;
    private String IDFriends;
    private int Status;

    public Friend(String IDUser, String IDFriends, int status) {
        this.IDUser = IDUser;
        this.IDFriends = IDFriends;
        Status = status;
    }

    public Friend(int ID, String IDUser, String IDFriends, int status) {
        this.ID = ID;
        this.IDUser = IDUser;
        this.IDFriends = IDFriends;
        Status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getIDFriends() {
        return IDFriends;
    }

    public void setIDFriends(String IDFriends) {
        this.IDFriends = IDFriends;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
