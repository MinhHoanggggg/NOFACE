package com.example.noface.service;


import com.example.noface.model.Posts;
import com.example.noface.model.Topic;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceAPI {
    String BASE_Service = "http://www.noface.somee.com/";

    @GET("get-all-topic")
    Observable<ArrayList<Topic>> GetAllTopic();

    @GET("post-trending")
    Observable<ArrayList<Posts>> PostTrending();

    @GET("get-all-post-by-id")
    Observable<ArrayList<Posts>> PostByTopic(@Query("id") int id);


}