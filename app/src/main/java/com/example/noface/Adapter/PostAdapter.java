package com.example.noface.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.PostActivity;
import com.example.noface.R;
import com.example.noface.model.Comment;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.model.User;
import com.example.noface.other.ItemClickListener;
import com.example.noface.other.SetAvatar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public PostAdapter(ArrayList<Posts> lstPost, Context context) {
        this.lstPost = lstPost;
        this.context = context;
    }


    private ArrayList<Posts> lstPost;
    private Context context;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_title.setText(lstPost.get(position).getTitle());
        holder.tvTime.setText(lstPost.get(position).getTime());

//      holder.txtCmt.setText(lstPost.get(position).getLikes().size());
        int idTopic =lstPost.get(position).getIDTopic();
        int idPost = lstPost.get(position).getIDPost();
        String idUser = lstPost.get(position).getIDUser();
        String date = lstPost.get(position).getTime();
        String title = lstPost.get(position).getTitle();
        String content = lstPost.get(position).getContent();
        setUserPost(idUser.trim(),holder.imgAvatar);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("idTopic",idTopic);
                intent.putExtra("idPost",idPost);
                intent.putExtra("idUser",idUser);
                intent.putExtra("date",date);
                intent.putExtra("title",title);
                intent.putExtra("content",content);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txt_title, tv_name, tvTime, txtCmt;
        public ImageView imgAvatar;


        public ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            tv_name = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tvTime);
            txtCmt = itemView.findViewById(R.id.txtCmt);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);

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
    public void setUserPost(String idUser,ImageView img){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = idUser;
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User lUser = snapshot.getValue(User.class);
                    SetAvatar.SetAva(img,lUser.getAvaPath());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
