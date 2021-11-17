package com.example.noface.model;

import com.google.gson.annotations.SerializedName;

public class Token {

    public Token(String token, String refreshToken) {
        this.token = token;
        RefreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    @SerializedName("data")
    private String token;
    private String RefreshToken;
}
