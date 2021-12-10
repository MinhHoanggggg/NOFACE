package com.example.noface.Adapter;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.PostActivity;
import com.example.noface.R;
import com.example.noface.model.Likes;
import com.example.noface.model.Message;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.ItemClickListener;
import com.example.noface.other.SetAvatar;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public PostAdapter(ArrayList<Posts> lstPost, ArrayList<Topic> lstTopic, Context context) {
        this.lstPost = lstPost;
        this.lstTopic = lstTopic;
        this.context = context;
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final ArrayList<Posts> lstPost;
    private final ArrayList<Topic> lstTopic;

    private final Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.CbLike.setChecked(false);
        holder.txt_title.setText(lstPost.get(position).getTitle());
        String t = lstPost.get(position).getTime();
        String tt = t.substring(8, 10) + "/" + t.substring(5, 7) + "/" + t.substring(0, 4) + " " + t.substring(11, 13) + ":" + t.substring(14, 16);
        holder.tvTime.setText(tt);

        holder.txtCmt.setText("Bình luận (" + lstPost.get(position).getComment().size() + ")");
        holder.txtlike.setText(String.valueOf(lstPost.get(position).getLikes().size()));

        ArrayList<Likes> alLikes = lstPost.get(position).getLikes();
        String id = user.getUid();
        for (Likes likes : alLikes) {
            holder.CbLike.setChecked(id.equals(likes.getIDUser().trim()));
        }



        int idTopic = lstPost.get(position).getIDTopic();
        for (Topic topic : lstTopic) {
            if(idTopic == topic.getIDTopic()){
                holder.txt_category.setText("#"+topic.getTopicName());
            }
        }
        int idPost = lstPost.get(position).getIDPost();
        String idUser = lstPost.get(position).getIDUser();
        setUserPost(idUser.trim(), holder.imgAvatar);

        holder.CbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dem = Integer.parseInt(holder.txtlike.getText().toString());
                if (!holder.CbLike.isChecked()) {
                    dem--;
                } else {
                    dem++;
                }
                holder.txtlike.setText(String.valueOf(dem));
                Like(idPost, user.getUid());
            }
        });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("idTopic", idTopic);
                intent.putExtra("idPost", idPost);
                intent.putExtra("idUser", idUser);
                intent.putExtra("likes", Integer.valueOf(holder.txtlike.getText().toString()));
                Boolean checkLike = holder.CbLike.isChecked();
                intent.putExtra("checklike", checkLike);
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstPost.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txt_title, tv_name, tvTime, txtCmt, txtlike,txt_category;
        public CheckBox CbLike;
        public ImageView imgAvatar;

        public ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            tv_name = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tvTime);
            txtlike = itemView.findViewById(R.id.txtlike);
            txtCmt = itemView.findViewById(R.id.txtCmt);
            CbLike = itemView.findViewById(R.id.CbLike);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txt_category = itemView.findViewById(R.id.txt_category);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getAdapterPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }
    }


    public void setUserPost(String idUser, ImageView img) {
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

    private void Like(int idPost, String idUser) {
        DataToken dataToken = new DataToken(context.getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.Like(dataToken.getToken(), idPost, idUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLike, this::handleError)
        );
    }

    private void handleResponseLike(Message message) {
        if (message.getStatus() != 1)
            Toast.makeText(context.getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(context.getApplicationContext(), "Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }


}
