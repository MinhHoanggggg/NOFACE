package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.noface.model.Token;
import com.example.noface.other.DataToken;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WelcomeActivity extends AppCompatActivity {
    private ImageView imgLogo;


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
                if( FirebaseAuth.getInstance().getCurrentUser() != null  ){
                    Login(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                else{
                    finish();
                    startActivity(new Intent(WelcomeActivity.this,StartActivity.class));
                }

            }
        }, 4500);




    }
    private void Login(String idUser) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetToken(idUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleError(Throwable throwable) {
    }

    private void handleResponse(Token token) {
        DataToken dataToken = new DataToken(WelcomeActivity.this);
        dataToken.saveToken(token.getToken(), token.getRefreshToken());
        finish();
        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
    }
}