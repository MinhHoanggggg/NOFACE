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
import com.squareup.picasso.Picasso;

public class SetAvatar {
    User lUser;

    public static  void SetAva(ImageView img, String path){
        if(!path.equals("")) {
            Picasso.get().load(path).into(img);
//            Glide.with(context).load(path).into(img); //You cannot start a load for a destroyed activity
        }else
            img.setImageResource(R.drawable.ic_user);
        }


}
