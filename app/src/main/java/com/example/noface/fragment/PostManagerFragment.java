package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noface.Adapter.PostAdapter;
import com.example.noface.Adapter.PostManagerAdapter;
import com.example.noface.R;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.service.ServiceAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostManagerFragment extends Fragment {
    private RecyclerView rcv_posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_posts_manager, container, false);
        rcv_posts = view.findViewById(R.id.rcv_posts);
//        danhSach = new ArrayList<>();
//        for(int i=1;i<=10;i++){
//            danhSach.add("");
//        }
        GetPostByTopic();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);

        return view;
    }

    //get data từ API
    private void GetPostByTopic() {

        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetPostByID(6)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );

    }

    private void handleResponse(ArrayList<Posts> posts) {
//
//        PostManagerAdapter adapter = new PostManagerAdapter(posts, getContext());
//        rcv_posts.setAdapter(adapter);
    }


    private void handleError(Throwable throwable) {
//        dismissProgressDialog();
        Toast.makeText(getContext(), "Sẽ cập nhật sau", Toast.LENGTH_SHORT).show();
    }
}