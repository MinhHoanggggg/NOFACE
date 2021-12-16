package com.example.noface.model;

public class Avatar {
    private String IDUser;
    private String URL;
    private String NameUser;

    public Avatar(String IDUser, String URL, String nameUser) {
        this.IDUser = IDUser;
        this.URL = URL;
        NameUser = nameUser;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getNameUser() {
        return NameUser;
    }

    public void setNameUser(String nameUser) {
        NameUser = nameUser;
    }
}
