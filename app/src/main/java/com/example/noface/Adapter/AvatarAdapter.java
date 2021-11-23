package com.example.noface.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noface.R;
import com.example.noface.model.Ava;
import com.example.noface.model.Avatar;
import com.example.noface.other.ItemClickListener;

import java.util.ArrayList;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {
   private Context context;
   private ArrayList<Ava> lstAva;
   private AvatarAdapterListener avatarAdapterListener;

    public AvatarAdapter(Context context, ArrayList<Ava> lstAva, AvatarAdapterListener avatarAdapterListener) {
        this.context = context;
        this.lstAva = lstAva;
        this.avatarAdapterListener = avatarAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext()))
                .inflate(R.layout.item_ava,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(lstAva.get(position).getImgurl()).into(holder.img_itemAva);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                avatarAdapterListener.click(v,pos);
            }
        });


    }

    @Override
    public int getItemCount() {
        return lstAva.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView img_itemAva;

        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_itemAva = itemView.findViewById(R.id.img_itemAva);

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
    public interface AvatarAdapterListener {
        void click(View v, int position);
    }


}
