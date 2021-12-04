package com.example.noface.Adapter;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.PostActivity;
import com.example.noface.R;
import com.example.noface.model.Likes;
import com.example.noface.model.Message;
import com.example.noface.model.Notification;
import com.example.noface.model.Posts;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.ItemClickListener;
import com.example.noface.other.SetAvatar;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Notification> lstNoti;
    private NotificationAdapterListener notificationAdapterListener;
    private String token;

    public NotificationAdapter(Context context, ArrayList<Notification> lstNoti) {
        this.context = context;
        this.lstNoti = lstNoti;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext()))
                .inflate(R.layout.item_noti,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (lstNoti.get(position).getStatus_Notification() ==0){
            holder.bg_back.setBackgroundColor(Color.parseColor("#E6B0C2"));
        }
        if(!lstNoti.get(position).getID_User_Seen_noti().trim().equals("Admin")) {
            setUINoti(lstNoti.get(position).getID_User_Seen_noti().trim(), holder.img_noti_ava);
        }
        else
            holder.img_noti_ava.setImageResource(R.drawable.logoreal);
        holder.txt_noti.setText(lstNoti.get(position).getData_Notification().trim());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                ShowNotifyUser.showProgressDialog(context,"Chờ xíuuuu");
                if (lstNoti.get(pos).getStatus_Notification() == 0) {

                    holder.bg_back.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    SeenNoti(token,lstNoti.get(pos).getID_Notification());
                }

                if (lstNoti.get(pos).getID_User_Seen_noti().trim().equals("Admin")){
                    ShowNotifyUser.dismissProgressDialog();
                    Toast.makeText(context, "Hệ thống: "+lstNoti.get(pos).getData_Notification().trim(), Toast.LENGTH_SHORT).show();
                }
                else{
                    GetPost(token,lstNoti.get(pos).getIDPost());
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return lstNoti.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CardView bg_back;
        private ImageView img_noti_ava;
        private TextView txt_noti;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_noti = itemView.findViewById(R.id.txt_noti);
            img_noti_ava = itemView.findViewById(R.id.img_noti_ava);
            bg_back = itemView.findViewById(R.id.bg_back);
            DataToken dataToken = new DataToken(context);
            token = dataToken.getToken();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getAdapterPosition());

        }
        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }
    }
    public interface NotificationAdapterListener {
        void click(View v, int position);
    }

    public void setUINoti(String idUser, ImageView img) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = idUser;
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User lUser = snapshot.getValue(User.class);
                SetAvatar.SetAva(img, lUser.getAvaPath());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void SeenNoti(String token,int idNoti) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.SeenNoti(token,idNoti)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Message message) {
        if(message.getStatus() == 0){
            Toast.makeText(context, "Lỗi stt", Toast.LENGTH_SHORT).show();
        }
    }
    private void GetPost(String token,int id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetPost(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Posts posts) {
        boolean checkLike = false;
        for (Likes likes : posts.getLikes()){
            if(likes.getIDUser().trim().equals(posts.getIDUser().trim())){
                checkLike = true;
            }
        }
        Intent intent = new Intent(context, PostActivity.class);
        intent.putExtra("idTopic", posts.getIDTopic());
        intent.putExtra("idPost", posts.getIDPost());
        intent.putExtra("likes", Integer.valueOf(posts.getLikes().size()));
        intent.putExtra("idUser", posts.getIDUser());
        intent.putExtra("checklike", checkLike);
        context.startActivity(intent);
        ShowNotifyUser.dismissProgressDialog();
    }


    private void handleError(Throwable throwable) {
    }

}
