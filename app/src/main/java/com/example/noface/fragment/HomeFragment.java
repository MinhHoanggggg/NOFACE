package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private RecyclerView rcv_posts;
    private ImageButton btn_create;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        rcv_posts = view.findViewById(R.id.rcv_posts);
        btn_create = view.findViewById(R.id.btn_create);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);

        ShowNotifyUser.showProgressDialog(getContext(),"Đang tải, đừng manh động...");
        //token
        DataToken dataToken = new DataToken(getContext());
        String token = dataToken.getToken();
        PostTrending(token);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CreatePost.class));
            }
        });

        return view;
    }

    //    get data từ API
    private void PostTrending(String token) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.PostTrending(token)
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
