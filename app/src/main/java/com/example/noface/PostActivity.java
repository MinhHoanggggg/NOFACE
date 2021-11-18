package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.CommentAdapter;
import com.example.noface.model.Comment;
import com.example.noface.model.Message;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
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
//import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostActivity extends AppCompatActivity{
    private TextView tvName, tvDate, tvTitle, tvContent, tvCate, tv_namePhake, txtcmtPhake, txtlike;
    private ImageView imgAvatar, imgAvatarUser, imgAvatarPhake, imgView;
    private EditText edt_cmt;
    private ImageButton btnSend, btnMenu;
    private RecyclerView rcv_cmt;
    private Button btnCmt;
    private CheckBox CbLike;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    User lUser,pUser;
    String idUser;
    int idTopic, sumLike, idPost, sumCmt;
    String date;
    String title;
    String content;
    Boolean checkLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tvName = findViewById(R.id.tvName);
        CbLike = findViewById(R.id.CbLike);
        tvCate = findViewById(R.id.tvCate);
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgAvatarUser = findViewById(R.id.imgAvatarUser);
        edt_cmt = findViewById(R.id.edt_cmt);
        btnSend = findViewById(R.id.btnSend);
        rcv_cmt = findViewById(R.id.rcv_cmt);
        tv_namePhake = findViewById(R.id.tv_namePhake);
        txtcmtPhake = findViewById(R.id.txtcmtPhake);
        imgAvatarPhake = findViewById(R.id.imgAvatarPhake);
        txtlike = findViewById(R.id.txtlike);
        btnCmt = findViewById(R.id.btnCmt);
        btnMenu = findViewById(R.id.btnMenu);
        imgView = findViewById(R.id.imgView);
        imgView.setVisibility(View.GONE); //VISIBLE

        Intent intent = getIntent();
         idUser = intent.getStringExtra("idUser");
         String idUs = idUser.trim();
         idTopic = intent.getIntExtra("idTopic", 0);
         idPost = intent.getIntExtra("idPost", 0);
         date = intent.getStringExtra("date");
         title = intent.getStringExtra("title");
         content = intent.getStringExtra("content");
         sumLike = intent.getIntExtra("likes", 0);
         checkLike = intent.getBooleanExtra("checklike",false);
        if(!idUs.equals(user.getUid())){
            btnMenu.setVisibility(View.GONE);
        }
         if(checkLike){
             CbLike.setChecked(true);
         }
        ShowNotifyUser.showProgressDialog(this,"Đang tải, đừng manh động...");
        setUI(idPost);

        int idpost = idPost;
        GetCmt(idpost);

        Intent intent1 = new Intent(PostActivity.this, ProfileUser.class);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent1.putExtra("idUser",idUser);
                startActivity(intent1);
            }
        });


        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent1.putExtra("idUser",idUser);
                startActivity(intent1);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetAvatar.SetAva(imgAvatarPhake,pUser.getAvaPath());
                tv_namePhake.setText(pUser.getName());
                txtcmtPhake.setText(edt_cmt.getText().toString());
                edt_cmt.setText("");

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());

                Comment comment = new Comment(0, idpost, user.getUid(), txtcmtPhake.getText().toString(), strDate);
                //gọi api gửi cmt
                SendCmt(comment);
            }
        });

        CbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dem = Integer.parseInt(txtlike.getText().toString());
                if(!CbLike.isChecked()){
                    dem--;
                }else{
                    dem++;
                }
                txtlike.setText(String.valueOf(dem));
                Like(idpost, user.getUid());
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), btnMenu);
                popup.inflate(R.menu.menu_edit_post);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return menuItemClicked(item);
                    }
                });
                // Show the PopupMenu.
                popup.show();
            }
        });
    }

    public void setUI(int id) {


        GetAllTopic();
        GetPost(id);


    }

    //=============================get Topic API=============================
    private void GetAllTopic() {
        DataToken dataToken = new DataToken(getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetAllTopic(dataToken.getToken())
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
        DataToken dataToken = new DataToken(getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetCmt(dataToken.getToken(), id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(ArrayList<Comment> comments) {
        try {
            //init rcv
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostActivity.this,
            LinearLayoutManager.VERTICAL, false);
            rcv_cmt.setLayoutManager(linearLayoutManager);
            //set adapter cho rcv
            CommentAdapter cmtAdapter = new CommentAdapter(comments, PostActivity.this);
            sumCmt = comments.size();
            btnCmt.setText(sumCmt + " bình luận");
            rcv_cmt.setAdapter(cmtAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
        ShowNotifyUser.dismissProgressDialog();
    }
    //=============================end get Cmt API===================================

    //=============================post Cmt API===================================

    private void SendCmt(Comment comment) {
        DataToken dataToken = new DataToken(getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.SendCmt(dataToken.getToken(), comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCMT, this::handleError)
        );
    }

    private void handleResponseCMT(Message message) {
        Toast.makeText(getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
        sumCmt++;
        btnCmt.setText(sumCmt + " bình luận");
    }
    //=============================end post Cmt API===================================

    //=============================get like API===================================
    private void Like(int idPost, String idUser) {
        DataToken dataToken = new DataToken(getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.Like(dataToken.getToken(), idPost, idUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLike, this::handleError)
        );
    }

    private void handleResponseLike(Message message) {
        if(message.getStatus() == 1)
            Toast.makeText(getApplicationContext(), "Thank you <3", Toast.LENGTH_SHORT).show();
        else{
            Toast.makeText(getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
        }
    }
    //=============================get like API===================================

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(this,"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }
    ////
    private void GetPost(int id) {
        DataToken dataToken = new DataToken(getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetPost(dataToken.getToken(), id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Posts posts) {
        tvDate.setText(posts.getTime());
        tvContent.setText(posts.getContent());
        tvTitle.setText(posts.getTitle());
        GetAllTopic();
        String id=  posts.getIDUser().trim();
        setUserPost(id);
        setUser(user);
        txtlike.setText(String.valueOf(sumLike));
        if (posts.getImagePost().length() > 15){
//            imgView.setVisibility(View.VISIBLE);
//            Picasso.get().load(posts.getImagePost()).into(imgView);
        }
        else {
            imgView.setVisibility(View.GONE);
        }
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
   ///MENU
   private boolean menuItemClicked(MenuItem item) {
       switch (item.getItemId()) {
           case R.id.itemEdit:
               Intent intent = new Intent(PostActivity.this,EditPost.class);
               intent.putExtra("idPost",idPost);
               startActivity(intent);
               break;
           case R.id.itemDel:
               showAlertDialog(this, "Bạn chắc chắn xóa bài viết này?");

               break;

           default:
               Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
               break;
       }
       return true;
   }

    private void DeletePost(int id) {
        DataToken dataToken = new DataToken(getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.DeletePost(dataToken.getToken(), id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Message message) {
        if(message.getStatus() == 1){
            Toast.makeText(getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlertDialog(Context context, String message){
        new AlertDialog.Builder(context)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        DeletePost(idPost);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}