package com.example.noface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.noface.fragment.CategoryFragment;
import com.example.noface.fragment.HomeFragment;
import com.example.noface.fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_CATEGORY = 1;
    private static final int FRAGMENT_PROFILE = 2;

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //set fragment cho từng item
        if(id == R.id.nav_home)
        {
            openHomeFragment();
        }else if(id == R.id.nav_category){
            openCategoryFragment();
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

    private void openCategoryFragment(){
        if(CurrentFragment != FRAGMENT_CATEGORY){
            replaceFragment(new CategoryFragment());
            CurrentFragment = FRAGMENT_CATEGORY;
        }
    }

    private void setTitleToolbar(){
        String title = "";
        switch (CurrentFragment){
            case FRAGMENT_HOME:
                title = "Trang chủ";
                break;

            case FRAGMENT_CATEGORY:
                title = "Chủ đề";
                break;
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












