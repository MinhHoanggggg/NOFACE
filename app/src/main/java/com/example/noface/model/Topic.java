package com.example.noface.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Topic {

    private int IDTopic;
    private String TopicName;
    private String Img;

    @SerializedName("lstPost")
    private List<Posts> Post;


    public Topic(int IDTopic, String topicName, String img, List<Posts> post) {
        this.IDTopic = IDTopic;
        TopicName = topicName;
        Img = img;
        Post = post;
    }

    public int getIDTopic() {
        return IDTopic;
    }

    public void setIDTopic(int IDTopic) {
        this.IDTopic = IDTopic;
    }

    public String getTopicName() {
        return TopicName;
    }

    public void setTopicName(String topicName) {
        TopicName = topicName;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String img) {
        Img = img;
    }

    public List<Posts> getPost() {
        return Post;
    }

    public void setPost(List<Posts> post) {
        Post = post;
    }
}
