package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostActivity extends AppCompatActivity{
    private TextView tvName, tvDate, tvTitle, tvContent, tvCate, txtlike;
    private ImageView imgAvatar, imgAvatarUser, imgView;
    private EditText edt_cmt;
    private ImageButton btnSend, btnMenu;
    private RecyclerView rcv_cmt;
    private Button btnCmt;
    private CheckBox CbLike;
    private LinearLayout lnlNOcmt;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    User lUser,pUser;
    String idUser, token;
    int idTopic, sumLike, idPost, sumCmt;
    String date;
    String title;
    String content;
    Boolean checkLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status("online");
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
        txtlike = findViewById(R.id.txtlike);
        btnCmt = findViewById(R.id.btnCmt);
        btnMenu = findViewById(R.id.btnMenu);
        imgView = findViewById(R.id.imgView);
        lnlNOcmt = findViewById(R.id.lnlNOcmt);
        lnlNOcmt.setVisibility(View.GONE); //VISIBLE

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

        DataToken dataToken = new DataToken(getApplicationContext());
        token = dataToken.getToken();

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
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());
                String cmt = edt_cmt.getText().toString();
                if(cmt.length() == 0){
                    Toast.makeText(getApplicationContext(), "Hãy nhập bình luận của bạn!", Toast.LENGTH_SHORT).show();
                }else if(cmt.length() > 100){
                    Toast.makeText(getApplicationContext(), "Bình luận của bạn quá dài!", Toast.LENGTH_SHORT).show();
                }else{
                    Comment comment = new Comment(0, idpost, user.getUid(), cmt, strDate, null);
                    //gọi api gửi cmt
                    SendCmt(comment);
                    edt_cmt.setText("");
                }
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

        new Timer().scheduleAtFixedRate(new NewsletterTask(), 0, 10000);
    }


    public class NewsletterTask extends TimerTask {
        @Override
        public void run() {
            GetCmt(idPost);
        }
    }

    public void setUI(int id) {
        GetAllTopic();
        GetPost(id);
    }

    //=============================get Topic API=============================
    private void GetAllTopic() {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetAllTopic(token)
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

        new CompositeDisposable().add(requestInterface.GetCmt(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(ArrayList<Comment> comments) {
        try {

            if (comments.size() == 0){
                lnlNOcmt.setVisibility(View.VISIBLE);
            }else
            {
                lnlNOcmt.setVisibility(View.GONE);
            }

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
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.SendCmt(token, comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCMT, this::handleError)
        );
    }

    private void handleResponseCMT(Message message) {
        Toast.makeText(getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
        sumCmt++;
        btnCmt.setText(sumCmt + " bình luận");
        GetCmt(idPost);
    }
    //=============================end post Cmt API===================================

    //=============================get like API===================================
    private void Like(int idPost, String idUser) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.Like(token, idPost, idUser)
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

    ////
    private void GetPost(int id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetPost(token, id)
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
            imgView.setVisibility(View.VISIBLE);
            Picasso.get().load(posts.getImagePost()).into(imgView);
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
                   SetAvatar.SetAva(imgAvatar,pUser.getAvaPath(),getApplicationContext());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}
    private void setUser(FirebaseUser user){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lUser = snapshot.getValue(User.class);
                    SetAvatar.SetAva(imgAvatarUser,lUser.getAvaPath(),PostActivity.this);
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
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.DeletePost(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Message message) {
        ShowNotifyUser.dismissProgressDialog();
        if(message.getStatus() == 1){
            Toast.makeText(getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,  MainActivity.class));
        }
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(PostActivity.this,"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }

    public void showAlertDialog(Context context, String message){
        new AlertDialog.Builder(context)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ShowNotifyUser.showProgressDialog(context,"Đang xóa bài viết, đừng manh động...");
                        DeletePost(idPost);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 2){
            assert data != null;
            int result = data.getIntExtra("result", 0);
            if (result == 1){
                GetCmt(idPost);
                ShowNotifyUser.dismissProgressDialog();
            }
        }
    }
}