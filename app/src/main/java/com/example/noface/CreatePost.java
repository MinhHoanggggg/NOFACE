package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.model.Img;
import com.example.noface.model.Message;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.SetAvatar;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class CreatePost extends AppCompatActivity {
    private ImageView imgAvatar, imgView;
    private TextView tvName;
    private EditText edtTitle, edtContent;
    private Spinner spnTopic;
    private Button btnCreate;
    private ImageButton btnBack, btnOpenfile, btnCancel;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User lUser;
    ArrayList<Integer> idTopic = new ArrayList<>();
    private Uri imageUri;
    String mUri="", strDate="";
    private StorageReference storageReference;
    private StorageTask uploadTask;
    Boolean oFile = false;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        status("online");
        imgAvatar = findViewById(R.id.imgAvatar);
        tvName = findViewById(R.id.tvName);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        spnTopic = findViewById(R.id.spnTopic);
        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);
        btnOpenfile = findViewById(R.id.btnOpenfile);
        imgView = findViewById(R.id.imgView);
        btnCancel = findViewById(R.id.btnCancel);
        constraintLayout = findViewById(R.id._layout);

        ShowNotifyUser.showProgressDialog(this,"Đang tải...");
        setUI(user);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(CreatePost.this, MainActivity.class));
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowNotifyUser.showProgressDialog(CreatePost.this, "Đang lưu bài viết...");
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strDate = sdf.format(c.getTime());
                if (edtTitle.getText().length()==0 || edtContent.getText().length()==0)
                {
                    ShowNotifyUser.dismissProgressDialog();
                    Toast.makeText(CreatePost.this, "Vui lòng nhập đầy đủ bài viết!", Toast.LENGTH_SHORT).show();
                } else if (oFile == true){
                    saveIMG();
                }
                else {
                    Posts posts = new Posts(0, idTopic.get((int)spnTopic.getSelectedItemId()),
                            user.getUid(), edtTitle.getText().toString(),
                            edtContent.getText().toString(), strDate, mUri, 0, null, null);
                    AddPost(posts);
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oFile = false; mUri = "";
                constraintLayout.setVisibility(View.GONE);
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        btnOpenfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile();
            }
        });
    }

    private void setUI(FirebaseUser user){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid();
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        GetAllTopic();
    }
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
        DataToken dataToken = new DataToken(getApplicationContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.AddPost(dataToken.getToken(), posts)
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


    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(this,"Không ổn rồi đại vương ơi! Đã có lỗi xảy ra");
    }

    //openFile
    private void openFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!= null){
            imageUri = data.getData();
            imgView.setImageURI(imageUri);
            constraintLayout.setVisibility(View.VISIBLE);
            oFile = true;
        } else{
            oFile = false;
            constraintLayout.setVisibility(View.GONE);
        }
    } //end.openFile

    //saveIMG
    private String getFileExtention (Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void saveIMG () {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString(); //SAve link img
                    Posts posts = new Posts(0, idTopic.get((int)spnTopic.getSelectedItemId()),
                            user.getUid(), edtTitle.getText().toString(),
                            edtContent.getText().toString(), strDate, mUri, 0, null, null);
                    AddPost(posts);
                    } else {
                        Toast.makeText(CreatePost.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreatePost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ShowNotifyUser.dismissProgressDialog();
            mUri="";
        }
    } //end.saveIMG

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