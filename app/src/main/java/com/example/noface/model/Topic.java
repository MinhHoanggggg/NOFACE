package com.example.noface.model;

import java.util.List;

public class Topic {

    public Topic(Integer idtopic, String topicName, String img, List<Posts> lstpost) {
        this.idtopic = idtopic;
        this.topicName = topicName;
        this.img = img;
        this.lstpost = lstpost;
    }

    public Integer getIdtopic() {
        return idtopic;
    }

    public void setIdtopic(Integer idtopic) {
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

    private Integer idtopic;
    private String topicName;
    private String img;
    private List<Posts> lstpost;
}
