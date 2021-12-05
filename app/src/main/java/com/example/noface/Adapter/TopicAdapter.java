package com.example.noface.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noface.MainActivity;
import com.example.noface.PostActivity;
import com.example.noface.R;
import com.example.noface.fragment.PostByTopicFragment;
import com.example.noface.inter.FragmentInterface;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.other.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder>{


    public TopicAdapter(ArrayList<Topic> lstTopic, Context context, TopicAdapterListener topicAdapterListener) {
        this.lstTopic = lstTopic;
        this.context = context;
        this.topicAdapterListener = topicAdapterListener;
    }

    private final ArrayList<Topic> lstTopic;
    private Context context;
    private TopicAdapterListener topicAdapterListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic lsttopic = lstTopic.get(position);

        holder.topic_txt.setText(lsttopic.getTopicName());
        Glide.with(context).load(lsttopic.getImg()).into(holder.topic_img);
//        Picasso.get().load(lsttopic.getImg())
//                .fit().centerCrop().error(R.drawable.ic_logo).into(holder.topic_img);

        holder.setItemClickListener(new ItemClickListener(){
            @Override
            public void onItemClick(View v, int pos) {
                topicAdapterListener.click(v,pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstTopic.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ItemClickListener itemClickListener;

        private TextView topic_txt;
        private ImageView topic_img;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topic_txt = itemView.findViewById(R.id.topic_txt);
            topic_img = itemView.findViewById(R.id.topic_img);

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
    public interface TopicAdapterListener{
        void click(View view,int position);
    }
}