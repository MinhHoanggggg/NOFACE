package com.example.noface.model;

public class Topic {

    public Topic(String idTopic, String nameTopic, String img) {
        IdTopic = idTopic;
        NameTopic = nameTopic;
        Img = img;
    }

    public String getIdTopic() {
        return IdTopic;
    }

    public void setIdTopic(String idTopic) {
        IdTopic = idTopic;
    }

    public String getNameTopic() {
        return NameTopic;
    }

    public void setNameTopic(String nameTopic) {
        NameTopic = nameTopic;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    private String IdTopic;
    private String NameTopic;
    private String Img;

}
