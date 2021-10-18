package com.example.noface.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noface.Adapter.PostAdapter;
import com.example.noface.Adapter.PostManagerAdapter;
import com.example.noface.R;

import java.util.ArrayList;

public class PostManagerFragment extends Fragment {
    private RecyclerView rcv_posts;
    private PostManagerAdapter postManagerAdapter;
    private ArrayList<String> danhSach;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_posts_manager, container, false);

        rcv_posts = view.findViewById(R.id.rcv_posts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);

        danhSach = new ArrayList<>();
        for(int i=1;i<=10;i++){
            danhSach.add("");
        }
        PostAdapter courseAdapter = new PostAdapter(danhSach, getContext());
        rcv_posts.setAdapter(courseAdapter);
        return view;
    }
}