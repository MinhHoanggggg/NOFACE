package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.PostAdapter;
import com.example.noface.CreatePost;
import com.example.noface.R;
import com.example.noface.inter.FragmentInterface;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostByTopicFragment extends Fragment {
    private RecyclerView rcv_posts;
    public int idTopic =0 ;
    String token;
    public ArrayList<Topic> lstTopic = new ArrayList<>();
    ImageView noface;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_post_by_topic, container, false);
        DataToken dataToken = new DataToken(getContext());
        token = dataToken.getToken();
        rcv_posts = view.findViewById(R.id.rcv_posts);
        noface = view.findViewById(R.id.noface);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);
        GetAllTopic();

        if (idTopic !=  0) {
            noface.setVisibility(View.GONE);
            PostByTopic(idTopic);
        } else {
            noface.setVisibility(View.VISIBLE);
        }
        ShowNotifyUser.showProgressDialog(getContext(),"??ang t???i, ?????ng mang ?????ng...");

        return view;
    }

    //    get data t??? API
    private void PostByTopic(int id) {

        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.PostByTopic(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
    }

    private void handleResponse1(ArrayList<Posts> posts) {
        try {

            PostAdapter postAdapter = new PostAdapter(posts, lstTopic, getContext());
            rcv_posts.setAdapter(postAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ShowNotifyUser.dismissProgressDialog();
    }

    private void GetAllTopic() {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetAllTopic(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ArrayList<Topic> topics) {
        for(int i =0; i<topics.size();i++ ){
            lstTopic.add(topics.get(i));
        }
        ShowNotifyUser.dismissProgressDialog();
    }

    public void showID(int id){
        idTopic = id;
    }
}
