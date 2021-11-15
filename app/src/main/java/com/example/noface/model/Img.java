package com.example.noface.model;

public class Img {
    private String idPost;
    private String imageUrl;

    public Img() {
    }

    public Img(String idPost, String imageUrl) {
        this.idPost = idPost;
        this.imageUrl = imageUrl;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
