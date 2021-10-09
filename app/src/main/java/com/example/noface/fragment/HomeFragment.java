package com.example.noface.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.HomeAdapter;
import com.example.noface.Adapter.PostAdapter;
import com.example.noface.MainActivity;
import com.example.noface.R;
import com.example.noface.model.Posts;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView rcv_posts;
    private HomeAdapter homeAdapter;
    private ArrayList<String> danhSach;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        rcv_posts = view.findViewById(R.id.rcv_posts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);
//        rcv_posts.setFocusable(false);
//
//        homeAdapter = new HomeAdapter(getContext(), getDataPosts());
//        rcv_posts.setAdapter(homeAdapter);

        danhSach = new ArrayList<>();
        for(int i=1;i<=10;i++){
            danhSach.add("");
        }
        PostAdapter courseAdapter = new PostAdapter(danhSach, getContext());
        rcv_posts.setAdapter(courseAdapter);
        return view;
    }

    //getData từ API
    private ArrayList<Posts> getDataPosts(){
        ArrayList<Posts> list = new ArrayList<>();
//        for (int i = 0; i < 20; i++){
//            list.add(new Posts("Đây là Title \n" + "nội dung demo nội dung demo nội dung demo nội dung demo nội dung demo nội dung demo" +
//                    "nội dung demo nội dung demo","nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung" +
//                    "nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung nội dung"));
//        }
        return list;
    }
}
