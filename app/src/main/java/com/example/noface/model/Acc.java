package com.example.noface.model;

public class Acc {
    private String IDUser;
    private String Name;
    private int Warning;
    private int Activated;


    public Acc(String IDUser, String name, int warning, int activated) {
        this.IDUser = IDUser;
        Name = name;
        Warning = warning;
        Activated = activated;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getWarning() {
        return Warning;
    }

    public void setWarning(int warning) {
        Warning = warning;
    }

    public int getActivated() {
        return Activated;
    }

    public void setActivated(int activated) {
        Activated = activated;
    }
}
