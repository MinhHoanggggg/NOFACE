package com.example.noface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.CategoryAdapter;
import com.example.noface.model.Category;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private ArrayList<Category> alCategory;
    private CategoryAdapter CategoryAdapter;
    private RecyclerView rcvCategoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        rcvCategoryList = findViewById(R.id.rcvCategoryList);

        //data mẫu
        alCategory = new ArrayList<>();
        for(int i = 1; i <= 29; i++ ){
            alCategory.add(new Category("Chủ đề"+ i, "chủ đề" + i));
        }

//        cuộn nuột hơn
        rcvCategoryList.setHasFixedSize(true);

        //truyền data qua adapter
        CategoryAdapter = new CategoryAdapter(alCategory, CategoryActivity.this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        rcvCategoryList.setLayoutManager(gridLayoutManager);
        rcvCategoryList.setAdapter(CategoryAdapter);
    }
}