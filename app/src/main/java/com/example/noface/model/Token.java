package com.example.noface.model;

import com.google.gson.annotations.SerializedName;

public class Token {

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @SerializedName("data")
    private String token;
}
