package com.example.noface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.noface.fragment.HomeFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int FRAGMENT_HOME = 0;

    private int CurrentFragment = FRAGMENT_HOME;

    private DrawerLayout drawer_layout;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set toolbar thay actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ánh xạ
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);

        //bắt sự kiện click icon home của nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                                                                R.string.nav_open, R.string.nav_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        //bắt sự kiện click item của nav
        nav_view.setNavigationItemSelectedListener(this);

        //mặc định home
        replaceFragment(new HomeFragment());
        nav_view.setCheckedItem(R.id.nav_home);
        setTitleToolbar();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //set fragment cho từng item
        switch(item.getItemId()) {
            case R.id.nav_home:
                openHomeFragment();
                break;
            case R.id.nav_category:
                startActivity(new Intent(this, CategoryActivity.class));
                break;

            default:break;
        }
        setTitleToolbar();

        //Đóng drawer
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void openHomeFragment(){
        if(CurrentFragment != FRAGMENT_HOME){
            replaceFragment(new HomeFragment());
            CurrentFragment = FRAGMENT_HOME;
        }
    }

    private void setTitleToolbar(){
        String title = "";
        switch (CurrentFragment){
            case FRAGMENT_HOME:
                title = "Trang chủ";
                break;

//            case ACTIVITY_CATEGORY:
//                title = "Chủ đề";
//                break;
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
}












