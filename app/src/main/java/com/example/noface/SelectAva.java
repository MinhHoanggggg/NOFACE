package com.example.noface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.example.noface.Adapter.AvatarAdapter;



import com.example.noface.fragment.ProfileFragment;
import com.example.noface.model.Avatar;
import com.example.noface.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SelectAva extends AppCompatActivity {
    private RecyclerView rclAva;
    private ArrayList<Avatar> lstAva = new ArrayList<>();
    private AvatarAdapter avatarAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_ava);
        rclAva = findViewById(R.id.rclAva);




        for(int i=1;i<11;i++){

            Avatar avatar = new Avatar(i,"ava"+i);
            lstAva.add(avatar);
        }
        avatarAdapter = new AvatarAdapter(SelectAva.this, lstAva, new AvatarAdapter.AvatarAdapterListener() {
            @Override
            public void click(View v, int position) {



            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        rclAva.setLayoutManager(gridLayoutManager);
        rclAva.setAdapter(avatarAdapter);
//        LinearLayoutManager linearLayoutManager =
//                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        rclAva.setLayoutManager(linearLayoutManager);
//        rclAva.setAdapter(avatarAdapter);

    }

}