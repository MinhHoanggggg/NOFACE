package com.example.noface;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends AppCompatActivity {
    TextView tvlike;
    Boolean f= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tvlike =findViewById(R.id.tvlike);
    }


    public void onClickBtn(View view) {
        tvlike.setText("Bạn và 1k\nHưởng ứng");
        if (f==false) {
            tvlike.setText("Bạn và 1k\nHưởng ứng");
            f=true;
        }
        else {
            tvlike.setText("1k\nHưởng ứng");
            f=false;
        }
    }
}