package com.example.noface.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class DataToken {

    private final Context context;

    public DataToken(Context context) {
        this.context = context;
    }

    public void saveToken(String token) {

        SharedPreferences settings = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", "Bearer " +  token);
        editor.putLong("expires", System.currentTimeMillis() + 86400000); //24 * 3600000
        editor.apply();
    }


    public String getToken() {
        SharedPreferences settings = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        long expires = settings.getLong("expires", 0);
        if (expires < System.currentTimeMillis()) {
            return "";
        }
        return settings.getString("token", "");
    }
}
