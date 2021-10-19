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

import com.example.noface.R;
import com.example.noface.model.Avatar;
import com.example.noface.other.ItemClickListener;

import java.util.ArrayList;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {
   private Context context;
   private ArrayList<Avatar> lstAva;
   private AvatarAdapterListener avatarAdapterListener;

    public AvatarAdapter(Context context, ArrayList<Avatar> lstAva, AvatarAdapterListener avatarAdapterListener) {
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
        switch (lstAva.get(position).getAvaPath()){
            case "ava1":
                holder.img_itemAva.setImageResource(R.drawable.ava1);
                break;
            case "ava2":
                holder.img_itemAva.setImageResource(R.drawable.ava2);
                break;
            case "ava3":
                holder.img_itemAva.setImageResource(R.drawable.ava3);
                break;
            case "ava4":
                holder.img_itemAva.setImageResource(R.drawable.ava4);
                break;
            case "ava5":
                holder.img_itemAva.setImageResource(R.drawable.ava5);
                break;
            case "ava6":
                holder.img_itemAva.setImageResource(R.drawable.ava6);
                break;
            case "ava7":
                holder.img_itemAva.setImageResource(R.drawable.ava7);
                break;
            case "ava8":
                holder.img_itemAva.setImageResource(R.drawable.ava8);
                break;
            case "ava9":
                holder.img_itemAva.setImageResource(R.drawable.ava9);
                break;
            case "ava10":
                holder.img_itemAva.setImageResource(R.drawable.ava10);
                break;
            default: break;
        }
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
