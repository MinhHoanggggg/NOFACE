package com.example.noface.model;

public class Category {
    public String getID_category() {
        return ID_category;
    }

    public void setID_category(String ID_category) {
        this.ID_category = ID_category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String ID_category;
    private String name;

    public Category(String ID_category, String name) {
        this.ID_category = ID_category;
        this.name = name;
    }

}
