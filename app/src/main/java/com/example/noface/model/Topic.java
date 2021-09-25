package com.example.noface.model;

public class Topic {

    public Topic(String ID_Topic, String name) {
        this.ID_Topic = ID_Topic;
        this.name = name;
    }

    public String getID_Topic() {
        return ID_Topic;
    }

    public void setID_Topic(String ID_Topic) {
        this.ID_Topic = ID_Topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String ID_Topic;
    private String name;

}
