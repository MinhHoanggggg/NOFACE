package com.example.noface.service;

import com.example.noface.model.Topic;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ServiceAPI {
    String BASE_Service = "https://192.168.1.10/";

    @GET("api/get-all-topic")
    Observable<ArrayList<Topic>> GetAllTopic();
}