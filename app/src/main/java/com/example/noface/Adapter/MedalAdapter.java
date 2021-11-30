package com.example.noface.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noface.R;
import com.example.noface.model.Ava;
import com.example.noface.model.Medals;
import com.example.noface.other.ItemClickListener;

import java.util.ArrayList;

public class MedalAdapter extends RecyclerView.Adapter<MedalAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Medals> lstMedal;
    private MedalAdapter.MedalAdapterListener medalAdapterListener;

    public MedalAdapter(Context context, ArrayList<Medals> lstMedal, MedalAdapterListener medalAdapterListener) {
        this.context = context;
        this.lstMedal = lstMedal;
        this.medalAdapterListener = medalAdapterListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext()))
                .inflate(R.layout.item_medal,parent,false);
        return new MedalAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(lstMedal.get(position).getImgMedal()).into(holder.img_item_Medal);
        holder.txtDesc.setText(lstMedal.get(position).getMedalName());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                medalAdapterListener.click(v,pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstMedal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView img_item_Medal;
        private TextView txtDesc;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_item_Medal = itemView.findViewById(R.id.img_item_Medal);
            txtDesc = itemView.findViewById(R.id.txtDesc);
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
    public interface MedalAdapterListener {
        void click(View v, int position);
    }
}
