package com.example.noface.model;

import java.util.ArrayList;

public class Achievement {
    private ArrayList<Medals> Medals;
    private int ID_Achievements;
    private String IDUser;
    private int IDMedal;


    public Achievement(ArrayList<Medals> medals, int ID_Achievements, String IDUser, int IDMedal) {
        Medals = medals;
        this.ID_Achievements = ID_Achievements;
        this.IDUser = IDUser;
        this.IDMedal = IDMedal;
    }

    public ArrayList<Medals> getMedals() {
        return Medals;
    }

    public void setMedals(ArrayList<Medals> medals) {
        Medals = medals;
    }

    public int getID_Achievements() {
        return ID_Achievements;
    }

    public void setID_Achievements(int ID_Achievements) {
        this.ID_Achievements = ID_Achievements;
    }

    public String getIDUser() {
        return IDUser;
    }

    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public int getIDMedal() {
        return IDMedal;
    }

    public void setIDMedal(int IDMedal) {
        this.IDMedal = IDMedal;
    }
}
