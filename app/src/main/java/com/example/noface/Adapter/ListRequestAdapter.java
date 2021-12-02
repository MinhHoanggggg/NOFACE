package com.example.noface.Adapter;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.PostActivity;
import com.example.noface.ProfileUser;
import com.example.noface.R;
import com.example.noface.model.Friend;
import com.example.noface.model.Message;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.ItemClickListener;
import com.example.noface.other.SetAvatar;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListRequestAdapter extends RecyclerView.Adapter<ListRequestAdapter.ViewHolder> {
    private final Context mContext;
    private final List<User> lUsers;
    private FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    String token = "";

    public ListRequestAdapter(Context mContext, List<User> lUsers) {
        this.mContext = mContext;
        this.lUsers = lUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        DataToken dataToken = new DataToken(mContext);
        token = dataToken.getToken();

        User user = lUsers.get(i);
        holder.tv_name.setText(user.getName());

        if (user.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_off.setVisibility(View.VISIBLE);
            holder.img_on.setVisibility(View.GONE);
        }

        SetAvatar.SetAva(holder.imgAvatar, user.getAvaPath());


        holder.tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (holder.getAdapterPosition());
                holder.tv_cancel.setVisibility(View.GONE);
                holder.tv_ok.setVisibility(View.GONE);
                holder.imgAvatar.setVisibility(View.GONE);
                holder.tv_name.setVisibility(View.GONE);
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);

                Friend acc = new Friend(0, fuser.getUid(), lUsers.get(position).getIdUser(), 1);
                Accept(token, acc);
            }
        });
        holder.tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (holder.getAdapterPosition());
                holder.tv_cancel.setVisibility(View.GONE);
                holder.tv_ok.setVisibility(View.GONE);
                holder.imgAvatar.setVisibility(View.GONE);
                holder.tv_name.setVisibility(View.GONE);
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);

//                Friend del = new Friend(0, lUsers.get(position).getIdUser(),  fuser.getUid(), 1);
//                DELfriend(del);
            }
        });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(mContext, ProfileUser.class);
                intent.putExtra("idUser", user.getIdUser());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_name, tv_cancel, tv_ok;
        public ImageView imgAvatar, img_on, img_off;
        public ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_name = itemView.findViewById(R.id.tv_name);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            tv_ok = itemView.findViewById(R.id.tv_ok);
            tv_cancel = itemView.findViewById(R.id.tv_cancel);
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, getAdapterPosition());
        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }
    }

    private void Accept(String token, Friend f) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.Accept(token, f)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void DELfriend(Friend f) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.DELfriend(token, f)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Message message) {
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.showAlertDialog(mContext, "Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }
}
