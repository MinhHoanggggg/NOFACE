package com.example.noface.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.R;
import com.example.noface.model.Comment;
import com.example.noface.model.Topic;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    public CommentAdapter(ArrayList<Comment> lstCmt, Context context) {
        this.lstCmt = lstCmt;
        this.context = context;
    }

    private final ArrayList<Comment> lstCmt;
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cmt, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtcmt.setText(lstCmt.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return lstCmt.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txtcmt, tv_name;
        private ImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtcmt = itemView.findViewById(R.id.txtcmt);
            tv_name = itemView.findViewById(R.id.tv_name);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
