package com.example.noface;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView imgLogo;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        imgLogo = findViewById(R.id.imgLogo);
        @SuppressLint("ResourceType")
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.animator.anim_rotate);
        imgLogo.startAnimation(animation);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              //  /Dieu huong//
                if(user != null){
                    finish();
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                }
                else{
                    finish();
                    startActivity(new Intent(WelcomeActivity.this,StartActivity.class));
                }

            }
        }, 4500);




    }
}