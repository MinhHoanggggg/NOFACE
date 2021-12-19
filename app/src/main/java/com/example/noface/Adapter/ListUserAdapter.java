package com.example.noface.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolder> {
    private final Context mContext;
    private final List<User> lUsers;
    String textString = "", flag = "seen";
    private boolean check;

    public ListUserAdapter(Context mContext, List<User> lUsers, boolean check) {
        this.mContext = mContext;
        this.lUsers = lUsers;
        this.check = check;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        User user = lUsers.get(i);


        int len = user.getIdUser().length();
        holder.tv_name.setText(user.getName()+" #"+user.getIdUser().substring(len-4, len));

        if (user.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_off.setVisibility(View.VISIBLE);
            holder.img_on.setVisibility(View.GONE);
        }
        SetAvatar.SetAva(holder.imgAvatar, user.getAvaPath());


        if (check) {
            newMessage(user.getIdUser(), holder.tv_new);
        } else {
            holder.tv_new.setVisibility(View.GONE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("userID", user.getIdUser());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lUsers.size();
    }

    private void newMessage(String id, TextView tv) {
        textString = "";
        flag = "seen";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    //last message
                    if (chat.getTo().equals(firebaseUser.getUid()) && chat.getFrom().equals(id) ||
                            chat.getTo().equals(id) && chat.getFrom().equals(firebaseUser.getUid())) {

                        if (chat.isImg() == true)
                            textString = "Đã gửi kèm hình ảnh...";
                        else
                            textString = chat.getMessage();
                    }

                    //Color - chưa xem
                    if (chat.isSeen() == false && chat.getTo().equals(firebaseUser.getUid()) && chat.getFrom().equals(id)) {
                        flag = "no";

                    }
                }

                switch (textString) {
                    case "":
                        tv.setText("");
                        break;
                    default:
                        tv.setText(textString);
                        break;
                }
                switch (flag) {
                    case "no":
                        tv.setTextColor(Color.parseColor("#F58365"));
                        break;
                    default:
                        tv.setTextColor(Color.parseColor("#737373"));
                        break;
                }
                textString = "";
                flag = "seen";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_name, tv_new;
        public ImageView imgAvatar, img_on, img_off;
        public ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_name = itemView.findViewById(R.id.tv_name);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            tv_new = itemView.findViewById(R.id.tv_new);
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
