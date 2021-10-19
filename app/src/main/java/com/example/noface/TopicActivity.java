package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.TopicAdapter;
import com.example.noface.model.Topic;
import com.example.noface.service.ServiceAPI;
import com.google.android.material.navigation.NavigationView;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TopicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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

        //init rcv
        rcvTopicList = findViewById(R.id.rcvTopicList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rcvTopicList.setLayoutManager(gridLayoutManager);

        //cuộn nuột hơn
        rcvTopicList.setHasFixedSize(true);

        //bắt sự kiện click icon home của nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        //bắt sự kiện click item của nav
        nav_view.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Chủ đề");

        //mặc định set category
        nav_view.setCheckedItem(R.id.nav_topic);

        //get data từ api
        GetAllTopic();
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

//    get data từ API
    private void GetAllTopic() {

        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetAllTopic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }




    private void handleResponse(ArrayList<Topic> topics) {
        try {
//            showProgressDialog(getApplicationContext(), "Đang tải dữ liệu");
            //set adapter cho rcv
            TopicAdapter topicAdapter = new TopicAdapter(topics, this);

            rcvTopicList.setAdapter(topicAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
//        dismissProgressDialog();
    }


    private void handleError(Throwable throwable) {
//        dismissProgressDialog();
        Toast.makeText(getApplicationContext(), "Đã có lỗi xãy ra!!!", Toast.LENGTH_SHORT).show();
    }

}