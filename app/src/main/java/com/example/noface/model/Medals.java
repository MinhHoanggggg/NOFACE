package com.example.noface.model;

public class Medals {
    private int IDMedal;
    private String MedalName;
    private String ImgMedal;
    private String Description;

    public Medals(int IDMedal, String medalName, String imgMedal, String description) {
        this.IDMedal = IDMedal;
        MedalName = medalName;
        ImgMedal = imgMedal;
        Description = description;
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
}
