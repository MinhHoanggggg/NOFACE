package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class CreatePost extends AppCompatActivity {
    private ImageView imgAvatar;
    private TextView tvName;
    private EditText edtTitle, edtContent;
    private Spinner spnTopic;
    private Button btnCreate;
    private ImageButton btnBack;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User lUser;
    ArrayList<Integer> idTopic = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        imgAvatar = findViewById(R.id.imgAvatar);
        tvName = findViewById(R.id.tvName);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        spnTopic = findViewById(R.id.spnTopic);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);

        ShowNotifyUser.showProgressDialog(this,"Đang tải...");
        setUI(user);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            ShowNotifyUser.showProgressDialog(v.getContext(),"Đang đăng bài viết...");
            int id = idTopic.get((int)spnTopic.getSelectedItemId());

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());

                Posts posts = new Posts(0, id,
                                        user.getUid(), edtTitle.getText().toString(),
                                        edtContent.getText().toString(), strDate, "", null, null);
                AddPost(posts);
            }
        });

    }
    private void setUI(FirebaseUser user){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid().toString();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lUser = snapshot.getValue(User.class);
                if (!lUser.getName().isEmpty()){
                    tvName.setText(lUser.getName());
                }
                if(lUser.getAvaPath()!=null){

                    SetAvatar.SetAva(imgAvatar,lUser.getAvaPath());
                }
                else
                    imgAvatar.setImageResource(R.drawable.ic_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        GetAllTopic();

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

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(this,"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }

    private void handleResponse(ArrayList<Topic> topics) {
        ShowNotifyUser.dismissProgressDialog();
        ArrayList<String> lstName = new ArrayList<>();

        for (int i =0; i<topics.size();i++){
            Topic topic = topics.get(i);

            if(topic !=null) {
               lstName.add(topic.getTopicName());
                idTopic.add(topic.getIDTopic());
            }
        }
        changeSpn(lstName);
    }
    public  void changeSpn(ArrayList<String> arraySpinner ){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTopic.setAdapter(adapter);
    }

    private void AddPost(Posts posts) {
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
        ShowNotifyUser.dismissProgressDialog();
        if(message.getStatus() == 0){
            ShowNotifyUser.showAlertDialog(this,"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
        }else{
            showAlertDialogVui(this, message.getNotification());
        }
    }

    public void showAlertDialogVui(Context context, String message){
        new AlertDialog.Builder(context)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(CreatePost.this, MainActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_input_add)
                .show();
    }
}