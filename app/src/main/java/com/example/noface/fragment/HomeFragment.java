package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.noface.Adapter.HomeAdapter;
import com.example.noface.Adapter.PostAdapter;
import com.example.noface.CreatePost;
import com.example.noface.MainActivity;
import com.example.noface.R;
import com.example.noface.model.Posts;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private Button btnCreatePost;
    private RecyclerView rcv_posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        rcv_posts = view.findViewById(R.id.rcv_posts);
        btnCreatePost = view.findViewById(R.id.btnCreatePost);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);

        ShowNotifyUser.showProgressDialog(getContext(),"Đang tải, đừng mang động...");
        //API data postrending
        PostTrending();

        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),CreatePost.class));
            }
        });
        return view;
    }

    //    get data từ API
    private void PostTrending() {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.PostTrending()
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
        ShowNotifyUser.dismissProgressDialog();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(getContext(),"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }
}
