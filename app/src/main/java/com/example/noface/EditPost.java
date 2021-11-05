package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.model.Message;
import com.example.noface.model.Posts;
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

public class EditPost extends AppCompatActivity {
    private EditText edt_post_Title,edt_post_Content;
    private Spinner spn_post_Cate;
    private TextView tv_post_Name,tv_post_Date;
    private ImageView img_post_Avatar;
    private ImageButton btn_post_Back;
    private Button btn_post_Save;
    private PostActivity postActivity;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private int idPost;

    private ArrayList<Integer> idTopics = new ArrayList<>();
    ArrayList<String> lstName = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        edt_post_Content = findViewById(R.id.edt_post_Content);
        edt_post_Title = findViewById(R.id.edt_post_Title);
        spn_post_Cate = findViewById(R.id.spn_post_Cate);
        tv_post_Name = findViewById(R.id.tv_post_Name);
        tv_post_Date = findViewById(R.id.tv_post_Date);
        img_post_Avatar = findViewById(R.id.img_post_Avatar);
        btn_post_Back = findViewById(R.id.btn_post_Back);
        btn_post_Save = findViewById(R.id.btn_post_Save);
        Intent intent = getIntent();
        idPost = intent.getIntExtra("idPost",0);
        //SetUI
        ShowNotifyUser.showProgressDialog(this,"Đang tải đợi xíu!!!");
        setUser(user);
        GetAllTopic();
        ///

        btn_post_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getIndex(spn_post_Cate,spn_post_Cate.getSelectedItem().toString());
                int idTopic = idTopics.get(index);
                Posts lPost = new Posts(idPost,idTopic,user.getUid(),edt_post_Title.getText().toString(),edt_post_Content.getText().toString()
                        ,tv_post_Date.getText().toString(), null,null, null);
                PostPost(lPost);
            //    postActivity.setUI(idPost);
            }
        });
        


    }
    private void setUser(FirebaseUser user) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               User lUser = snapshot.getValue(User.class);
                tv_post_Name.setText(lUser.getName());
                 if (lUser.getAvaPath() != null) {
                    SetAvatar.SetAva(img_post_Avatar, lUser.getAvaPath());
                } else
                    img_post_Avatar.setImageResource(R.drawable.ic_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void GetPost(int id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetPost(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

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

    private void PostPost(Posts posts) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.AddPost(posts)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Message message) {

        Toast.makeText(getApplicationContext(), message.getNotification(), Toast.LENGTH_SHORT).show();
    }

    private void handleResponse(ArrayList<Topic> topics) {


        for (int i =0; i<topics.size();i++){
            Topic topic = topics.get(i);

            if(topic !=null) {
                lstName.add(topic.getTopicName());
                idTopics.add(topic.getIDTopic());
            }
        }
        changeSpn(lstName);
        GetPost(idPost);
    }


    private void handleResponse(Posts posts) {
        edt_post_Content.setText(posts.getContent());
       edt_post_Title.setText(posts.getTitle());
       tv_post_Date.setText(posts.getTime());
       int pos = 0;
       for (int i=0; i<idTopics.size(); i++){
           int id = idTopics.get(i);
           if(id == posts.getIDTopic()){
               pos = i;
           }
       }
       spn_post_Cate.setSelection(pos);

        ShowNotifyUser.dismissProgressDialog();
    }


    private void handleError(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Không ổn gòi đại vương ơi !!!", Toast.LENGTH_SHORT).show();
    }

    public  void changeSpn(ArrayList<String> arraySpinner ){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_post_Cate.setAdapter(adapter);

    }
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}