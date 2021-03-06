package com.example.noface.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.ChatActivity;
import com.example.noface.R;
import com.example.noface.model.Chat;
import com.example.noface.model.User;
import com.example.noface.other.ItemClickListener;
import com.example.noface.other.SetAvatar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private static final int _LEFT = 0;
    private static final int _RIGHT = 1;
    private final Context mContext;
    private final List<Chat> lChat;
    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String imgAvatar;

    public MessageAdapter(Context mContext, List<Chat> lChat, String imgAvatar) {
        this.mContext = mContext;
        this.lChat = lChat;
        this.imgAvatar = imgAvatar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == _RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Chat chat = lChat.get(i);

        SetAvatar.SetAva(holder.imgAvatar, imgAvatar);

        if (i == lChat.size()-1){
            if (chat.isSeen()){
                holder.imgAvatar.setVisibility(View.VISIBLE);
                SetAvatar.SetAva(holder.imgAvatar, imgAvatar);
            } else {
                holder.txt_seen.setVisibility(View.VISIBLE);
                holder.txt_seen.setText("???? g???i");
            }
        } else {
            holder.imgAvatar.setVisibility(View.GONE);
            holder.txt_seen.setVisibility(View.GONE);
        }

        if (chat.isImg() == true){
            Picasso.get().load(chat.getMessage()).into(holder.img_message);
            holder.img_message.setVisibility(View.VISIBLE);
            holder.txt_message.setVisibility(View.GONE);
        } else {
            holder.img_message.setVisibility(View.GONE);
            holder.txt_message.setVisibility(View.VISIBLE);
            holder.txt_message.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return lChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_message, txt_seen;
        public CircleImageView imgAvatar;
        public ImageView img_message;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_message = itemView.findViewById(R.id.txt_message);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            img_message = itemView.findViewById(R.id.img_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (lChat.get(position).getFrom().equals(id))
            return _RIGHT;
        else
            return _LEFT;
    }
}
