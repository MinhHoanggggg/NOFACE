package com.example.noface.other;

import android.media.Image;
import android.widget.ImageView;

import com.example.noface.R;

public class SetAvatar {
    public void SetAva(ImageView img, String path){

            switch (path){
                case "ava1":
                    img.setImageResource(R.drawable.ava1);
                    break;
                case "ava2":
                    img.setImageResource(R.drawable.ava2);
                    break;
                case "ava3":
                    img.setImageResource(R.drawable.ava3);
                    break;
                case "ava4":
                    img.setImageResource(R.drawable.ava4);
                    break;
                case "ava5":
                    img.setImageResource(R.drawable.ava5);
                    break;
                case "ava6":
                    img.setImageResource(R.drawable.ava6);
                    break;
                case "ava7":
                    img.setImageResource(R.drawable.ava7);
                    break;
                case "ava8":
                    img.setImageResource(R.drawable.ava8);
                    break;
                case "ava9":
                    img.setImageResource(R.drawable.ava9);
                    break;
                case "ava10":
                    img.setImageResource(R.drawable.ava10);
                    break;
                default: break;
            }
        }

}
