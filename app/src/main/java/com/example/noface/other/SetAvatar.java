package com.example.noface.other;

import android.content.Context;
import android.media.Image;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.noface.R;
import com.example.noface.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetAvatar {
    User lUser;

    public static  void SetAva(ImageView img, String path,Context context){
        if(!path.equals("")) {
            Glide.with(context).load(path).into(img);
        }else
            img.setImageResource(R.drawable.ic_user);
        }


}
