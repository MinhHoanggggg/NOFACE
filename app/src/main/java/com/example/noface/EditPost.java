package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
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
    private ImageView img_post_Avatar, imgView;
    private ImageButton btn_post_Back, btnOpenfile;
    private Button btn_post_Save;
    private int idPost;
    private Uri imageUri;
    String mUri="";
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        btnOpenfile = findViewById(R.id.btnOpenfile);
        imgView = findViewById(R.id.imgView);
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
                saveIMG();
//                int index = getIndex(spn_post_Cate,spn_post_Cate.getSelectedItem().toString());
//                int idTopic = idTopics.get(index);
//                Posts lPost = new Posts(idPost,idTopic,user.getUid(),edt_post_Title.getText().toString(),edt_post_Content.getText().toString()
//                        ,tv_post_Date.getText().toString(), null,null, null);
//                PostPost(lPost);
            //    postActivity.setUI(idPost);
            }
        });

        //Openfile
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        btnOpenfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });
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
        }
    } //end.openFile

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
                        ShowNotifyUser.showProgressDialog(EditPost.this, "Đang lưu bài viết...");
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString(); //SAve link img

//                        Img uploadImg = new Img("test", mUri); // LUU
//                        String uploadId = reference.push().getKey(); // TRU
//                        reference.child(uploadId).setValue(uploadImg);//FIREBASE
//                        Toast.makeText(CreatePost.this, "Đã lưu!", Toast.LENGTH_SHORT).show();
//                        ShowNotifyUser.dismissProgressDialog();
                        int index = getIndex(spn_post_Cate, spn_post_Cate.getSelectedItem().toString());
                        int idTopic = idTopics.get(index);
                        Posts lPost = new Posts(idPost, idTopic, user.getUid(), edt_post_Title.getText().toString(),
                                edt_post_Content.getText().toString(), tv_post_Date.getText().toString(),
                                mUri, null, null);
                        PostPost(lPost);
                    } else {
                        Toast.makeText(EditPost.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditPost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mUri="";
        }
    } //end.saveIMG


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

    private void PostPost(Posts posts) {
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
        startActivity(new Intent(this, MainActivity.class));
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