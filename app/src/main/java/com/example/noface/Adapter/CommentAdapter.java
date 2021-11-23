package com.example.noface.Adapter;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.EditCmtActivity;
import com.example.noface.R;
import com.example.noface.model.Comment;
import com.example.noface.model.LikeComment;
import com.example.noface.model.Message;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.example.noface.model.User;
import com.example.noface.other.SetAvatar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public CommentAdapter(ArrayList<Comment> lstCmt, Context context) {
        this.lstCmt = lstCmt;
        this.context = context;
    }

    private final ArrayList<Comment> lstCmt;
    private final Context context;
    private String token;
    private int idPost;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cmt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.CbLikeCmt.setChecked(false);
        holder.txtcmt.setText(lstCmt.get(position).getContent());
        holder.txtlikeCmt.setText((lstCmt.get(position).getLikeComment().size() + " lượt thích"));
        int idcmt = lstCmt.get(position).getIDCmt();

        ArrayList<LikeComment> likeComments = lstCmt.get(position).getLikeComment();
        String id = user.getUid();
        for (LikeComment likes : likeComments) {
            holder.CbLikeCmt.setChecked(id.equals(likes.getIDUser().trim()));
        }

        if (!user.getUid().equals(lstCmt.get(position).getIDUser().trim())) {
            holder.btnMenuCmt.setVisibility(View.GONE);
        }

        DataToken dataToken = new DataToken(context.getApplicationContext());
        token = dataToken.getToken();

        String idUserCmt = lstCmt.get(position).getIDUser();
        setUserCmt(idUserCmt.trim(), holder.imgAvatar);

        holder.CbLikeCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dem = Integer.parseInt(holder.txtlikeCmt.getText().toString().replaceAll(" lượt thích", ""));
                if (!holder.CbLikeCmt.isChecked()) {
                    dem--;
                } else {
                    dem++;
                }
                holder.txtlikeCmt.setText(((dem) + " lượt thích"));
                LikeCmt(idcmt, user.getUid());
            }
        });

        holder.btnMenuCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context.getApplicationContext(), holder.btnMenuCmt);
                popup.inflate(R.menu.menu_cmt);

                idPost = lstCmt.get(holder.getAdapterPosition()).getIDPost();
                int idCmt = lstCmt.get(holder.getAdapterPosition()).getIDCmt();
                String cmt = lstCmt.get(holder.getAdapterPosition()).getContent();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return menuItemClicked(item, idCmt, cmt, idPost);
                    }
                });
                // Show the PopupMenu.
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstCmt.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txtcmt, tv_name, txtlikeCmt;
        private final ImageView imgAvatar;
        private final ImageButton btnMenuCmt;
        private final CheckBox CbLikeCmt;

        public ViewHolder(View itemView) {
            super(itemView);
            txtcmt = itemView.findViewById(R.id.txtcmt);
            tv_name = itemView.findViewById(R.id.tv_name);
            imgAvatar = itemView.findViewById(R.id.img_cmt_Avatar);
            btnMenuCmt = itemView.findViewById(R.id.btnMenuCmt);
            CbLikeCmt = itemView.findViewById(R.id.CbLikeCmt);
            txtlikeCmt = itemView.findViewById(R.id.txtlikeCmt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }


    private void LikeCmt(int idcmt, String idUser) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.LikeCmt(token, idcmt, idUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLike, this::handleError)
        );
    }

    private void handleResponseLike(Message message) {
        if (message.getStatus() != 1)
            Toast.makeText(context.getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
    }

    ///MENU
    private boolean menuItemClicked(MenuItem item, int idCmt, String cmt, int idPost) {
        switch (item.getItemId()) {
            case R.id.itemEdit:
                Intent intent = new Intent(context, EditCmtActivity.class);
                intent.putExtra("idpost", idPost);
                intent.putExtra("id", idCmt);
                intent.putExtra("cmt", cmt);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle();
                ((Activity) context).startActivityForResult(intent, 1, bundle);
                break;

            case R.id.itemDel:
                showAlertDialog(context, idCmt, "Bạn chắc chắn xóa bình luận này?");
                break;
        }
        return true;
    }

    public void showAlertDialog(Context context, int idCmt, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ShowNotifyUser.showProgressDialog(context, "Đang xóa bình luận, đừng manh động...");
                        DeleteCmt(idCmt);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void DeleteCmt(int idcmt) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.DeleteCmt(token, idcmt)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCmt, this::handleError)
        );
    }

    private void handleResponseCmt(Message message) {
        if (message.getStatus() == 1) {
            GetCmt(idPost);
        } else {
            ShowNotifyUser.showAlertDialog(context.getApplicationContext(), "Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
        }
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(context.getApplicationContext(), "Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }

    private void GetCmt(int id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetCmt(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(ArrayList<Comment> comments) {
        new CommentAdapter(comments, context);
        ShowNotifyUser.dismissProgressDialog();
        Toast.makeText(context.getApplicationContext(), "Xóa bình luận thành công!", Toast.LENGTH_SHORT).show();
    }

    public void setUserCmt(String idUser, ImageView img) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = idUser;
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User lUser = snapshot.getValue(User.class);
                SetAvatar.SetAva(img, lUser.getAvaPath());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
