package com.example.noface.model;

import java.util.List;

public class Topic {

    public Topic(String idtopic, String topicName, String img, List<Posts> lstpost) {
        this.idtopic = idtopic;
        this.topicName = topicName;
        this.img = img;
        this.lstpost = lstpost;
    }

    public String getIdtopic() {
        return idtopic;
    }

    public void setIdtopic(String idtopic) {
        this.idtopic = idtopic;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<Posts> getLstpost() {
        return lstpost;
    }

    public void setLstpost(List<Posts> lstpost) {
        this.lstpost = lstpost;
    }

    private String idtopic;
    private String topicName;
    private String img;
    private List<Posts> lstpost;
}
