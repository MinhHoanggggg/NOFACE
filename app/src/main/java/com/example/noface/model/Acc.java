package com.example.noface.model;

public class Acc {
    public Acc(String IDUser, String name, String avt, int warning, int activated) {
        this.IDUser = IDUser;
        Name = name;
        Avt = avt;
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

    public String getAvt() {
        return Avt;
    }

    public void setAvt(String avt) {
        Avt = avt;
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

    private String IDUser;
    private String Name;
    private String Avt;
    private int Warning;
    private int Activated;
}
