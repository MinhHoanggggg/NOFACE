package com.example.noface.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.R;
import com.example.noface.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private ArrayList<Category> lstCategory;
    private Context context;

    public CategoryAdapter(ArrayList<Category> lstCategory, Context context) {
        this.lstCategory = lstCategory;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_title.setText("1");
        holder.txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Đã chọn chủ đề", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstCategory.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_title = itemView.findViewById(R.id.txt_title);
        }
    }
}