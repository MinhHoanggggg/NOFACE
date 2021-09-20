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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.CategoryAdapter;
import com.example.noface.fragment.HomeFragment;
import com.example.noface.model.Category;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<Category> alCategory;
    private CategoryAdapter CategoryAdapter;
    private RecyclerView rcvCategoryList;

    private DrawerLayout drawer_layout;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //set toolbar thay actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ánh xạ
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        rcvCategoryList = findViewById(R.id.rcvCategoryList);

        //bắt sự kiện click icon home của nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        //bắt sự kiện click item của nav
        nav_view.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Chủ đề");

        //mặc định category
        nav_view.setCheckedItem(R.id.nav_category);

        //data mẫu
        alCategory = new ArrayList<>();
        for(int i = 1; i <= 29; i++ ){
            alCategory.add(new Category("Chủ đề"+ i, "chủ đề" + i));
        }

        //cuộn nuột hơn
        rcvCategoryList.setHasFixedSize(true);

        //truyền data qua adapter
        CategoryAdapter = new CategoryAdapter(alCategory, CategoryActivity.this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        rcvCategoryList.setLayoutManager(gridLayoutManager);
        rcvCategoryList.setAdapter(CategoryAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //set fragment cho từng item
        switch(item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_category:
                startActivity(new Intent(this, CategoryActivity.class));
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