package com.example.noface.other;

import static android.content.Context.MODE_PRIVATE;
import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.noface.LoginActivity;
import com.example.noface.MainActivity;
import com.example.noface.model.Token;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataToken {

    private final Context context;

    public DataToken(Context context) {
        this.context = context;
    }

    public void saveToken(String token, String refreshToken) {

        SharedPreferences settings = context.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", "Bearer " +  token);
        editor.putLong("expires", System.currentTimeMillis() + 7200000); //2h * 3600000
        editor.putString("refreshToken", refreshToken);
        editor.putLong("expiresRefreshToken", System.currentTimeMillis() + 601200000); //7d * 24h * 3600000
        editor.apply();
    }


    public String getToken() {
        SharedPreferences settings = context.getSharedPreferences("data", MODE_PRIVATE);
        long expires = settings.getLong("expires", 0);
        if (expires < System.currentTimeMillis()) {
            long expiresRefreshToken = settings.getLong("expiresRefreshToken", 0);
            if(expiresRefreshToken < System.currentTimeMillis()){
                FirebaseAuth.getInstance().signOut();
                ((Activity)context).finish();
                context.startActivity(new Intent(context, LoginActivity.class));
                Toast.makeText(context.getApplicationContext(), "Bạn đã hết phiên đăng nhập, vui lòng đăng nhập lại",
                Toast.LENGTH_SHORT).show();
            }
            String f5Token = settings.getString("refreshToken", "");
            RefreshToken(f5Token);
        }
        return settings.getString("token", "");
    }

    private void RefreshToken(String f5Token) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.RefreshToken(f5Token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Token token) {
        saveToken(token.getToken(), token.getRefreshToken());
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.showAlertDialog(context.getApplicationContext(), "Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }

}
