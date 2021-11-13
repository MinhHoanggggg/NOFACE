package com.example.noface.model;

public class Acc {
    private String IDUser;
    private String Name;

    public Acc(String IDUser, String Name) {
        this.IDUser = IDUser;
        this.Name = Name;
    }


    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getName1() {
        return Name;
    }

    public void setName1(String name) {
        Name = name;
    }
}
