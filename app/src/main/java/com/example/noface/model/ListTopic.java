package com.example.noface.model;

import java.util.ArrayList;

public class ListTopic {
    public ListTopic(ArrayList<Topic> topics) {
        this.topics = topics;
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
    }

    private ArrayList<Topic> topics;
}