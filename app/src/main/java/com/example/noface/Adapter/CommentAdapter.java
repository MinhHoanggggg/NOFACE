package com.example.noface.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.R;
import com.example.noface.model.Comment;
import com.example.noface.model.Topic;
import com.example.noface.model.User;
import com.example.noface.other.SetAvatar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    public CommentAdapter(ArrayList<Comment> lstCmt, Context context) {
        this.lstCmt = lstCmt;
        this.context = context;
    }

    private final ArrayList<Comment> lstCmt;
    private Context context;
    String path = "";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cmt, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtcmt.setText(lstCmt.get(position).getContent());
        String id = lstCmt.get(position).getIDUser();
        setUserCmt(id.trim(), holder.imgAvatar);

    }

    @Override
    public int getItemCount() {
        return lstCmt.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txtcmt, tv_name, txtlikeCmt;
        private ImageView imgAvatar;
        private ImageButton btnMenuCmt;
        private CheckBox CbLikeCmt;

        public ViewHolder(View itemView) {
            super(itemView);
            txtcmt = itemView.findViewById(R.id.txtcmt);
            tv_name = itemView.findViewById(R.id.tv_name);
            imgAvatar = itemView.findViewById(R.id.img_cmt_Avatar);
            btnMenuCmt = itemView.findViewById(R.id.btnMenuCmt);
            CbLikeCmt = itemView.findViewById(R.id.CbLikeCmt);
            txtlikeCmt = itemView.findViewById(R.id.txtlikeCmt);
        }

        @Override
        public void onClick(View view) {

        }
    }
    public void setUserCmt(String idUser,ImageView img){
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
