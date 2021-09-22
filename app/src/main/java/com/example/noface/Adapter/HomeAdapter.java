package com.example.noface.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.R;
import com.example.noface.model.Posts;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder>{

    private Context context;
    private ArrayList<Posts> lstPosts;


    public HomeAdapter(Context context, ArrayList<Posts> lstPosts) {
        this.context = context;
        this.lstPosts = lstPosts;
    }

//    public HomeAdapter(Context context) {
//        this.context = context;
//    }

    public void setData(ArrayList<Posts> list){
        this.lstPosts = list;
        //f5 data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Posts posts = lstPosts.get(position);
        if(posts == null){
               return;
        }
        holder.txt_title.setText(posts.getTitle());
        holder.txt_title_inside.setText(posts.getTitle());
        holder.txt_content.setText(posts.getContent());

        holder.folding_cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.folding_cell.toggle(false);
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

    public class HomeViewHolder extends RecyclerView.ViewHolder{

        //khai báo id item của item_home
        private FoldingCell folding_cell;
        private TextView txt_title, txt_content, txt_title_inside;


        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            folding_cell = itemView.findViewById(R.id.folding_cell);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_title_inside = itemView.findViewById(R.id.txt_title_inside);
            txt_content = itemView.findViewById(R.id.txt_content);
        }
    }
}
