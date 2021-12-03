package com.example.noface.service;


import com.example.noface.model.Acc;
import com.example.noface.model.Achievements;
import com.example.noface.model.Comment;
import com.example.noface.model.Friend;
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
    String BASE_Service = "http://noface.somee.com/";

    @POST("get-token/{idUser}")
    Observable<Token> GetToken(@Query("idUser") String idUser);

    @POST("refresh-token")
    Observable<Token> RefreshToken(@Body Token token);

    @GET("get-all-topic")
    Observable<ArrayList<Topic>> GetAllTopic(@Header("Authorization") String token);

    @GET("post-trending")
    Observable<ArrayList<Posts>> PostTrending(@Header("Authorization") String token);

    @GET("new-post")
    Observable<ArrayList<Posts>> home(@Header("Authorization") String token);

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

    @POST("like-cmt/{idcmt}/{iduser}")
    Observable<Message> LikeCmt(@Header("Authorization") String token,
                                @Query("idcmt") int idcmt,
                                @Query("iduser") String iduser);

    @DELETE("delete-cmt/{idcmt}")
    Observable<Message> DeleteCmt(@Header("Authorization") String token,
                                  @Query("idcmt") int idcmt);

    @POST("Check-Achievements/{iduser}")
    Observable<Message> CheckAchie(@Header("Authorization") String token,
                                   @Query("iduser") String iduser);

    @GET("DanhHieu/{iduser}")
    Observable<ArrayList<Achievements>> GetMedals(@Header("Authorization") String token,
                                                  @Query("iduser") String iduser);

    @GET("CheckFriends/{iduser}/{idfriend}")
    Observable<Message> GetCheckFr(@Header("Authorization") String token,
                                   @Query("iduser") String uid,
                                   @Query("idfriend") String fid);

    @POST("Follower")
    Observable<Message> addFriends(@Header("Authorization") String token,
                                   @Body Friend friend);

    @POST("DeleteFriend")
    Observable<Message> DELfriend(@Header("Authorization") String token,
                                  @Body Friend friend);

    @POST("Accept")
    Observable<Message> Accept(@Header("Authorization") String token,
                               @Body Friend friend);

    @POST("List-Friend")
    Observable<ArrayList<Friend>> listFriend(@Header("Authorization") String token,
                                             @Query("idUser") String id);

    @POST("List-Follower")
    Observable<ArrayList<Friend>> FriendsRequest(@Header("Authorization") String token,
                                                 @Query("idUser") String id);

}