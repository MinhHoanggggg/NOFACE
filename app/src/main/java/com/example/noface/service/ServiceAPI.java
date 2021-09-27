package com.example.noface.service;

import com.example.noface.model.ListTopic;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ServiceAPI {
    String BASE_Service = "https://localhost:44351/";

    @GET("api/get-all-topic")
    Observable<ListTopic> GetAllTopic();
}