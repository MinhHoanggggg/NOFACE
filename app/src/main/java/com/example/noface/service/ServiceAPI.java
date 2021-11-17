package com.example.noface.service;


import com.example.noface.model.Acc;
import com.example.noface.model.Comment;
import com.example.noface.model.Likes;
import com.example.noface.model.Message;
import com.example.noface.model.Posts;
import com.example.noface.model.Token;
import com.example.noface.model.Topic;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceAPI {
    String BASE_Service = "http://apinoface.somee.com/";

    @POST("get-token/{idUser}")
    Observable<Token> GetToken(@Query("idUser") String idUser);

    @POST("refresh-token")
    Observable<Token> RefreshToken(@Body String f5Token);

    @GET("get-all-topic")
    Observable<ArrayList<Topic>> GetAllTopic(@Header("Authorization") String token);

    @GET("post-trending")
    Observable<ArrayList<Posts>> PostTrending(@Header("Authorization") String token);

    @GET("get-all-post-by-id/{id}")
    Observable<ArrayList<Posts>> PostByTopic(@Header("Authorization") String token,
                                             @Query("id") int id);

    @POST("post-post")
    Observable<Message> AddPost(@Header("Authorization") String token,
                                @Body Posts posts);

    @GET("get-all-post-by-user/{id}")
    Observable<ArrayList<Posts>> Wall(@Header("Authorization") String token,
                                      @Query("id") String id);

    @GET("get-all-cmt-by-id/{id}")
    Observable<ArrayList<Comment>> GetCmt(@Header("Authorization") String token,
                                          @Query("id") int id);

    @POST("post-cmt")
    Observable<Message> SendCmt(@Header("Authorization") String token,
                                @Body Comment comment);

    @GET("get-post-by-id/{id}")
    Observable<Posts> GetPost(@Header("Authorization") String token,
                              @Query("id") int id);

    @POST("like-post/{idpost}/{iduser}")
    Observable<Message> Like(@Header("Authorization") String token,
                             @Query("idpost") int idpost,
                             @Query("iduser") String iduser);

    @POST("create-user")
    Observable<Message> CreateUser(@Body Acc acc);

    @DELETE("delete-post/{id}")
    Observable<Message> DeletePost(@Header("Authorization") String token,
                                   @Query("id") int id);
}