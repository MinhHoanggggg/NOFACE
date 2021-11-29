package com.example.noface.model;

import java.util.ArrayList;

public class Medals {


    public Medals(ArrayList<com.example.noface.model.Achievements> achievements, int IDMedal, String medalName, String imgMedal, String description) {
        Achievements = achievements;
        this.IDMedal = IDMedal;
        MedalName = medalName;
        ImgMedal = imgMedal;
        Description = description;
    }

    public ArrayList<com.example.noface.model.Achievements> getAchievements() {
        return Achievements;
    }

    public void setAchievements(ArrayList<com.example.noface.model.Achievements> achievements) {
        Achievements = achievements;
    }

    public int getIDMedal() {
        return IDMedal;
    }

    public void setIDMedal(int IDMedal) {
        this.IDMedal = IDMedal;
    }

    public String getMedalName() {
        return MedalName;
    }

    public void setMedalName(String medalName) {
        MedalName = medalName;
    }

    public String getImgMedal() {
        return ImgMedal;
    }

    public void setImgMedal(String imgMedal) {
        ImgMedal = imgMedal;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    private ArrayList<Achievements> Achievements;
    private int IDMedal;
    private String MedalName;
    private String ImgMedal;
    private String Description;

}
