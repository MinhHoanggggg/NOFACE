package com.example.noface;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.TopicAdapter;
import com.example.noface.model.Topic;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TopicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Topic> alTopic;
    private TopicAdapter TopicAdapter;
    private RecyclerView rcvTopicList;

    private DrawerLayout drawer_layout;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        //set toolbar thay actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ánh xạ
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        rcvTopicList = findViewById(R.id.rcvTopicList);

        //bắt sự kiện click icon home của nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        //bắt sự kiện click item của nav
        nav_view.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Chủ đề");

        //mặc định category
        nav_view.setCheckedItem(R.id.nav_topic);

        //data mẫu
        alTopic = new ArrayList<>();
        for(int i = 1; i <= 29; i++ ){
            alTopic.add(new Topic("Chủ đề"+ i, "chủ đề" + i));
        }

        //cuộn nuột hơn
        rcvTopicList.setHasFixedSize(true);

        //truyền data qua adapter
        TopicAdapter = new TopicAdapter(alTopic, TopicActivity.this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        rcvTopicList.setLayoutManager(gridLayoutManager);
        rcvTopicList.setAdapter(TopicAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //set fragment cho từng item
        switch(item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_topic:
                startActivity(new Intent(this, TopicActivity.class));
                break;
            default:break;
        }

        //Đóng drawer
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START);
        }else {
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}