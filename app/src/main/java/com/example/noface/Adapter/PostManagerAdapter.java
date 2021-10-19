package com.example.noface.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.PostActivity;
import com.example.noface.R;
import com.example.noface.model.Posts;
import com.example.noface.other.ItemClickListener;

import java.util.ArrayList;

public class PostManagerAdapter extends RecyclerView.Adapter<PostManagerAdapter.ViewHolder>{

    public PostManagerAdapter(Context context, ArrayList<Posts> lstPosts) {
        this.context = context;
        this.lstPosts = lstPosts;
    }

    private Context context;
    private ArrayList<Posts> lstPosts;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_title.setText("Title "+position);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                context.startActivity(new Intent(context, PostActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(lstPosts != null) {
            return lstPosts.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView txt_title, tv_name;

        public ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            tv_name = itemView.findViewById(R.id.tv_name);
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
}
