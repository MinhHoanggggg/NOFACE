package com.example.noface.service;


import com.example.noface.model.Comment;
import com.example.noface.model.Likes;
import com.example.noface.model.Message;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceAPI {
    String BASE_Service = "http://www.noface.somee.com/";

    @GET("get-all-topic")
    Observable<ArrayList<Topic>> GetAllTopic();

    @GET("post-trending")
    Observable<ArrayList<Posts>> PostTrending();

    @GET("get-all-post-by-id/{id}")
    Observable<ArrayList<Posts>> PostByTopic(@Query("id") int id);

    @POST("post-post")
    Observable<Message> AddPost(@Body Posts posts);

    @GET("get-all-post-by-user/{id}")
    Observable<ArrayList<Posts>> Wall(@Query("id") String id);

    @GET("get-all-cmt-by-id/{id}")
    Observable<ArrayList<Comment>> GetCmt(@Query("id") int id);

    @POST("post-cmt")
    Observable<Message> SendCmt(@Body Comment comment);

    @GET("get-post-by-id/{id}")
    Observable<Posts> GetPost(@Query("id") int id);

    @POST("like-post/{idpost}/{iduser}")
    Observable<Message> Like(@Query("idpost") int idpost,
                             @Query("iduser") String iduser);
}