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
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataToken {

    private final Context context;
    boolean isTokenExpired = false;

    public DataToken(Context context) {
        this.context = context;
    }

    public void saveToken(String token, String refreshToken) {

        SharedPreferences settings = context.getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", "Bearer " +  token);
        editor.putLong("expires", System.currentTimeMillis() + 7200000); //2h * 3600000    7200000
        editor.putString("refreshToken", refreshToken);
        editor.putLong("expiresRefreshToken", System.currentTimeMillis() + 601200000); //7d * 24h * 3600000    601200000
        editor.apply();
    }


    public String getToken() {
//        //xếp hàng
        if(isTokenExpired){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//            if(isTokenExpired){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        if(!isTokenExpired){
            SharedPreferences settings = context.getSharedPreferences("data", MODE_PRIVATE);
            long expires = settings.getLong("expires", 0);
            if (expires < System.currentTimeMillis()) {
                isTokenExpired = true;
                long expiresRefreshToken = settings.getLong("expiresRefreshToken", 0);
                if(expiresRefreshToken < System.currentTimeMillis()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    String id = user.getUid();
                    getToken(id);
                }else{
                    String f5Token = settings.getString("refreshToken", "");
                    RefreshToken(f5Token);
                }
            }else{
                return settings.getString("token", "");
            }
        }

        return getToken();
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
        isTokenExpired = false;
    }

    private void getToken(String idUser) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetToken(idUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(Token token) {
        DataToken dataToken = new DataToken(context.getApplicationContext());
        dataToken.saveToken(token.getToken(), token.getRefreshToken());
        isTokenExpired = false;
    }


    private void handleError(Throwable throwable) {
        ShowNotifyUser.showAlertDialog(context.getApplicationContext(), "Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }

}
