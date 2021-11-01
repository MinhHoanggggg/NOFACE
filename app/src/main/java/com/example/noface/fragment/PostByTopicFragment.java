package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.PostAdapter;
import com.example.noface.CreatePost;
import com.example.noface.R;
import com.example.noface.inter.FragmentInterface;
import com.example.noface.model.Posts;
import com.example.noface.service.ServiceAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostByTopicFragment extends Fragment{
    private Button btnCreatePost;
    private RecyclerView rcv_posts;
    int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        rcv_posts = view.findViewById(R.id.rcv_posts);
        btnCreatePost = view.findViewById(R.id.btnCreatePost);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);

        Bundle bundle = getArguments();
        if(bundle != null){
            PostByTopic(bundle.getInt("id"));
        }

//        //API data postrending
//        PostByTopic(id);
//        ///
        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreatePost.class));
            }
        });
        return view;
    }

    //    get data từ API
    private void PostByTopic(int id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.PostByTopic(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ArrayList<Posts> posts) {
        try {

            PostAdapter postAdapter = new PostAdapter(posts, getContext());
            rcv_posts.setAdapter(postAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
//        dismissProgressDialog();
    }

    private void handleError(Throwable throwable) {

    }
}
