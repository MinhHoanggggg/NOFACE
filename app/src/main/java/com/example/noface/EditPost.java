package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditPost extends AppCompatActivity {
    private EditText edt_post_Title, edt_post_Content;
    private Spinner spn_post_Cate;
    private TextView tv_post_Name, tv_post_Date;
    private ImageView img_post_Avatar, imgView;
    private ImageButton btn_post_Back, btnOpenfile, btnCancel;
    private Button btn_post_Save;
    private int idPost, idTopic;
    private Uri imageUri;
    String mUri = "", strDate = "";
    private StorageReference storageReference;
    private StorageTask uploadTask;
    Boolean oFile = false;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ConstraintLayout constraintLayout;
    private ArrayList<Integer> idTopics = new ArrayList<>();
    ArrayList<String> lstName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        status("online");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        Intent intent = getIntent();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        strDate = sdf.format(c.getTime());
        idPost = intent.getIntExtra("idPost", 0);
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
        constraintLayout = findViewById(R.id._layout);
        btnCancel = findViewById(R.id.btnCancel);
        //SetUI
        ShowNotifyUser.showProgressDialog(this, "??ang t???i ?????i x??u!!!");
        setUser(user);
        GetAllTopic();
        ///

        btn_post_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowNotifyUser.showProgressDialog(EditPost.this, "??ang l??u b??i vi???t...");
                int index = getIndex(spn_post_Cate, spn_post_Cate.getSelectedItem().toString());
                idTopic = idTopics.get(index);
                if (edt_post_Title.getText().length() == 0 || edt_post_Content.getText().length() == 0) {
                    ShowNotifyUser.dismissProgressDialog();
                    Toast.makeText(EditPost.this, "Vui l??ng nh???p ?????y ????? b??i vi???t!", Toast.LENGTH_SHORT).show();
                } else if (oFile == true) {
                    saveIMG();
                } else {
                    Posts lPost = new Posts(idPost, idTopic, user.getUid(), edt_post_Title.getText().toString(),
                            edt_post_Content.getText().toString(), strDate,
                            mUri, 0, null, null);
                    PostPost(lPost);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oFile = false;
                mUri = "";
                constraintLayout.setVisibility(View.GONE);
            }
        });
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
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgView.setImageURI(imageUri);
            constraintLayout.setVisibility(View.VISIBLE);
            oFile = true;
        } else {
            oFile = false;
            constraintLayout.setVisibility(View.GONE);
        }
    } //end.openFile

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveIMG() {
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
                        ShowNotifyUser.showProgressDialog(EditPost.this, "??ang l??u b??i vi???t...");
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString(); //SAve link img
                        Posts lPost = new Posts(idPost, idTopic, user.getUid(), edt_post_Title.getText().toString(),
                                edt_post_Content.getText().toString(), strDate,
                                mUri, 0, null, null);
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
            mUri = "";
            ShowNotifyUser.dismissProgressDialog();
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
        for (int i = 0; i < topics.size(); i++) {
            Topic topic = topics.get(i);
            if (topic != null) {
                lstName.add(topic.getTopicName());
                idTopics.add(topic.getIDTopic());
            }
        }
        changeSpn(lstName);
        GetPost(idPost);
    }

    public void changeSpn(ArrayList<String> arraySpinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_post_Cate.setAdapter(adapter);

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

    private void handleResponse(Posts posts) {
        edt_post_Content.setText(posts.getContent());
        edt_post_Title.setText(posts.getTitle());
        tv_post_Date.setText(posts.getTime());
        int pos = 0;
        for (int i = 0; i < idTopics.size(); i++) {
            int id = idTopics.get(i);
            if (id == posts.getIDTopic()) {
                pos = i;
            }
        }
        spn_post_Cate.setSelection(pos);
        if (posts.getImagePost().length() > 15) {
            constraintLayout.setVisibility(View.VISIBLE);
            Picasso.get().load(posts.getImagePost()).into(imgView);
        }

        ShowNotifyUser.dismissProgressDialog();
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
        ShowNotifyUser.dismissProgressDialog();
        if (message.getStatus() == 0) {
            Toast.makeText(EditPost.this, "Kh??ng ???n r???i ?????i v????ng ??i! ???? c?? l???i x???y ra", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(EditPost.this, MainActivity.class));
        }
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        Toast.makeText(getApplicationContext(), "Kh??ng ???n g??i ?????i v????ng ??i !!!", Toast.LENGTH_SHORT).show();
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
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
}