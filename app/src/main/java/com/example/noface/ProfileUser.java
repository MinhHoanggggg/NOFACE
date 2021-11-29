package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.Adapter.PostAdapter;
import com.example.noface.model.Friend;
import com.example.noface.model.Message;
import com.example.noface.model.Posts;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.SetAvatar;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileUser extends AppCompatActivity {
    private int flag=-1;
    private RecyclerView rcv_posts_user;
    String idUser="", token="";
    private User lUser;
    private ImageView img_user_Ava;
    private TextView txtNameUser, btnFr;
    private ImageButton btnUserBack,btn_chat;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status("online");
        setContentView(R.layout.activity_profile_user);
        rcv_posts_user = findViewById(R.id.rcv_posts_user);
        btnUserBack = findViewById(R.id.btnUserBack);
        btn_chat = findViewById(R.id.btn_chat);
        img_user_Ava = findViewById(R.id.img_user_Ava);
        txtNameUser =  findViewById(R.id.txtNameUser);
        btnFr =  findViewById(R.id.btnFr);
        ///Get ID
        Intent intent = getIntent();
        idUser = intent.getStringExtra("idUser");
        //
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcv_posts_user.setLayoutManager(linearLayoutManager);
        //Set UI
        DataToken dataToken = new DataToken(ProfileUser.this);
        token = dataToken.getToken();
        Check(user.getUid(), idUser.trim());
        Wall(idUser.trim());
        setUI(idUser.trim());
        ShowNotifyUser.showProgressDialog(this,"Đang tải, đừng manh động...");
        btnUserBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileUser.this, ChatActivity.class);
                intent.putExtra("userID", idUser.trim());
                startActivity(intent);
            }
        });



        btnFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (flag){
                    case 3:
                        showYNDialog(ProfileUser.this, "Đại vương có muốn hủy kết bạn?", flag);
                        break;
                    case 2:
                        showYNDialog(ProfileUser.this, "Yes, đó là bạn tôi", flag);
                        break;
                    case 0:
                        showYNDialog(ProfileUser.this, "Bạn có chắc là muốn kết bạn với người này?", flag);
                        break;
                    case 1:
                        showYNDialog(ProfileUser.this, "Hủy gửi yêu cầu", flag);
                        break;
                    default:
                        btnFr.setVisibility(View.GONE);
                        break;
                }

            }
        });
    }

    //    get data từ API
    private void setUI(String idUser) {
        if (user.getUid().equals(idUser)){
            btn_chat.setVisibility(View.GONE);
            btnFr.setVisibility(View.GONE);
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = idUser;
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lUser = snapshot.getValue(User.class);
                if (!lUser.getName().isEmpty()) {
                    txtNameUser.setText(lUser.getName());
                }
                if (lUser.getAvaPath() != null) {
                    SetAvatar.SetAva(img_user_Ava, lUser.getAvaPath());
                } else
                    img_user_Ava.setImageResource(R.drawable.ic_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Wall(String userid) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.Wall(token, userid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ArrayList<Posts> posts) {
        try {
            PostAdapter postAdapter = new PostAdapter(posts, ProfileUser.this);
            rcv_posts_user.setAdapter(postAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
        ShowNotifyUser.dismissProgressDialog();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(this,"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }

    //Friend
    private void Check(String uid, String fid) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetCheckFr(token, uid, fid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCheck, this::handleError)
        );
    }

    private void handleResponseCheck(Message message) {
        flag = message.getStatus();
        switch (message.getStatus()){
            case 3:
                setColor(true, "Bạn bè");
                break;
            case 2:
                setColor(false, "Chấp nhận kết bạn?");
                break;
            case 0:
                setColor(true, "Người lạ");
                break;
            case 1:
                setColor(false, "Đã gửi kết bạn");
                break;
            default:
                btnFr.setVisibility(View.GONE);
                break;
        }
    }

    public void showYNDialog(Context context, String message, int n){
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận")
                .setMessage(message)
                .setIcon(R.drawable.ic_logo)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (n){
                            case 3:
                                Friend delF = new Friend(0, user.getUid(), idUser.trim(), 3);
                                DELfriend(delF);
                                break;
                            case 2:
                                Friend acc = new Friend(0, user.getUid(), idUser.trim(), 1);
                                Accept(acc);
                                break;
                            case 0:
                                Friend friend = new Friend(0, user.getUid(), idUser.trim(), 1);
                                AddFriend(friend);
                                break;
                            case 1: //huy gui yeu cau ket bạn
                                setColor(true, "Người lạ");

//                                Friend del = new Friend(user.getUid(), idUser.trim(), 3);
//                                DELfriend(del);
                                break;
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void AddFriend(Friend f) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.addFriends(token, f)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAddfr, this::handleError)
        );
    }

    private void handleResponseAddfr(Message message) {
        if (message.getStatus() == 1 ){
            setColor(false, "Đã gửi kết bạn");
        }
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
                .subscribe(this::handleResponseDEL, this::handleError)
        );
    }

    private void handleResponseDEL(Message message) {
        if (message.getStatus() == 1){
            setColor(true, "Người lạ");
        }
    }

    private void Accept(Friend f) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.Accept(token, f)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAcc, this::handleError)
        );
    }

    private void handleResponseAcc(Message message) {
        if (message.getStatus() == 1){
            setColor(true, "Bạn bè");
        }
    }

    private void setColor(Boolean f, String test){
        btnFr.setText(test);
        if(f){
            btnFr.setTextColor(Color.parseColor("#1C1C1C"));
            btnFr.setBackgroundResource(R.drawable.custom_tv);
        } else {
            btnFr.setBackgroundColor(Color.parseColor("#D96AC2"));
            btnFr.setTextColor(Color.parseColor("#FFFFFFFF"));
        }
    }
    //===============================
    private void status(String status) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child("Users/" + user.getUid() + "/status").setValue(status);
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}