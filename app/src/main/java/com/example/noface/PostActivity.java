package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.CommentAdapter;
import com.example.noface.Adapter.TopicAdapter;
import com.example.noface.model.Comment;
import com.example.noface.model.Topic;
import com.example.noface.model.User;
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

public class PostActivity extends AppCompatActivity {
    private TextView tvName, tvDate, tvTitle, tvContent,tvCate;
    private ImageView imgAvatar, imgAvatarUser;
    private EditText edt_cmt;
    private ImageButton btnSend;
    private RecyclerView rcv_cmt;
    private Button btnLike,btnCmt;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    User lUser,pUser;
    String idUser;
    int idTopic;
    int idPost;
    String date;
    String title;
    String content;

    Boolean f = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tvName = findViewById(R.id.tvName);
        btnLike = findViewById(R.id.btnLike);
        btnCmt = findViewById(R.id.btnCmt);
        tvCate = findViewById(R.id.tvCate);
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatarUser = findViewById(R.id.imgAvatarUser);
        edt_cmt = findViewById(R.id.edt_cmt);
        btnSend = findViewById(R.id.btnSend);
        rcv_cmt = findViewById(R.id.rcv_cmt);

        Intent intent = getIntent();
         idUser = intent.getStringExtra("idUser");
         idTopic = intent.getIntExtra("idTopic", 0);
         idPost = intent.getIntExtra("idPost", 0);
         date = intent.getStringExtra("date");
         title = intent.getStringExtra("title");
         content = intent.getStringExtra("content");
        setUI();

        int idpost = idPost;
        GetCmt(idpost);
    }


//    public void onClickBtn(View view) {
//        tvlike.setText("Bạn và 1k\nHưởng ứng");
//        if (f == false) {
//            tvlike.setText("Bạn và 1k\nHưởng ứng");
//            f = true;
//        } else {
//            tvlike.setText("1k\nHưởng ứng");
//            f = false;
//        }
//    }

    public void setUI() {
        ShowNotifyUser.showProgressDialog(this,"Đang tải, đừng manh động...");
        tvDate.setText(date);
        tvContent.setText(content);
        tvTitle.setText(title);
        GetAllTopic();
        setUserPost(idUser.trim());
        setUser(user);
    }

    //=============================get Topic API=============================
    private void GetAllTopic() {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetAllTopic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ArrayList<Topic> topics) {
        for(int i =0; i<topics.size();i++ ){
            Topic topic = topics.get(i);
            int id = topic.getIDTopic();
            if(topic !=null && id == idTopic) {

                tvCate.setText(topic.getTopicName());

            }
        }
        ShowNotifyUser.dismissProgressDialog();

    }
    //=============================end get Topic API=============================

    //=============================get Cmt API===================================
    private void GetCmt(int id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetCmt(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(ArrayList<Comment> comments) {
        try {

            //set adapter cho rcv
            CommentAdapter cmtAdapter = new CommentAdapter(comments, PostActivity.this);
            rcv_cmt.setAdapter(cmtAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
        ShowNotifyUser.dismissProgressDialog();
    }
    //=============================end get Cmt API===================================

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(this,"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }

    private void setUserPost(String idUser){

//         Uri photoUrl = user.getPhotoUrl();
//         Glide.with(this.getActivity()).load(photoUrl).error(R.drawable.ic_user).into(imgAva);
        //
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = idUser;
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pUser = snapshot.getValue(User.class);
                if (!pUser.getName().isEmpty()){
                    tvName.setText(pUser.getName());
                }

                if(pUser.getAvaPath()!=null){
                   SetAvatar.SetAva(imgAvatar,pUser.getAvaPath());
                }
                else
                    imgAvatar.setImageResource(R.drawable.ic_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}
    private void setUser(FirebaseUser user){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid().toString();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lUser = snapshot.getValue(User.class);

                if(lUser.getAvaPath()!=null){

                    SetAvatar.SetAva(imgAvatarUser,lUser.getAvaPath());
                }
                else
                    imgAvatarUser.setImageResource(R.drawable.ic_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}