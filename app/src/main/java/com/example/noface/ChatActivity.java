package com.example.noface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.Adapter.ListUserAdapter;
import com.example.noface.Adapter.MessageAdapter;
import com.example.noface.model.Chat;
import com.example.noface.model.Posts;
import com.example.noface.model.User;
import com.example.noface.other.SetAvatar;
import com.example.noface.other.ShowNotifyUser;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView imgAvatarUser, btnSend;
    private TextView txtUserName;
    private EditText txtMessage;
    private RecyclerView rcvChat;
    private ImageView imgView, btnopen;
    private Uri imageUri;
    Boolean oFile = false;
    MessageAdapter adapter;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String userID, mUri="";
    List<Chat> lChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        status("online");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        imgAvatarUser = findViewById(R.id.imgAvatar);
        btnSend = findViewById(R.id.btnSend);
        txtUserName = findViewById(R.id.txtName);
        txtMessage = findViewById(R.id.txtMessage);
        rcvChat = findViewById(R.id.rcvChat);
        btnopen = findViewById(R.id.btnopen);
        imgView = findViewById(R.id.imgView);
        rcvChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true); //hiển thị phần tử cuối cùng
        rcvChat.setLayoutManager(linearLayoutManager);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getData();

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = txtMessage.getText().toString();
                if (oFile == false && !mess.equals("")){
                    sendMess(false, user.getUid(), userID, txtMessage.getText().toString());
                    txtMessage.setText("");
                } else {
                    saveIMG ();

                }

                //done
                imgView.setVisibility(View.GONE);
                oFile = false;
            }
        });

        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ProfileUser.class);
                intent.putExtra("idUser", userID);
                startActivity(intent);
            }
        });
        imgAvatarUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ProfileUser.class);
                intent.putExtra("idUser", userID);
                startActivity(intent);
            }
        });

        btnopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile();
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgView.setVisibility(View.GONE);
                oFile = false;  mUri = ""; imageUri = null;
            }
        });
    }

    private void getData(){
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID"); //người nhận
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(userID);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User lUser = snapshot.getValue(User.class);
                txtUserName.setText(lUser.getName());
                if (lUser.getAvaPath()!=null)
                    SetAvatar.SetAva(imgAvatarUser, lUser.getAvaPath());

                readMessage(user.getUid(), userID, lUser.getAvaPath());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage(user.getUid(), userID); //update trạng thái xem
    }

    private void sendMess(Boolean fimg, String I, String you, String mess){
        DatabaseReference reference = firebaseDatabase.getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("from", I);
        hashMap.put("to", you);
        hashMap.put("message", mess);
        hashMap.put("seen", false);
        hashMap.put("img", fimg);
        reference.child("Chat").push().setValue(hashMap);
    }

    private void readMessage(String myID, String id, String img){
        lChat = new ArrayList<>();
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chat");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lChat.clear();
                for (DataSnapshot dataSnapshot :  snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getFrom().equals(myID) && chat.getTo().equals(id)
                    || chat.getFrom().equals(id) && chat.getTo().equals(myID)){
                        lChat.add(chat);
                    }
                    adapter= new MessageAdapter(ChatActivity.this, lChat, img);
                    rcvChat.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void seenMessage(String my, String id) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chat");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getTo().equals(my) && chat.getFrom().equals(id)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            imgView.setVisibility(View.VISIBLE);
            oFile = true;
        } else{
            oFile = false;
            imgView.setVisibility(View.GONE);
        }
    } //end.openFile

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
                        mUri = downloadUri.toString(); //save link img

                        String mess = txtMessage.getText().toString();
                        if (!mess.equals(""))
                            sendMess(false, user.getUid(), userID, txtMessage.getText().toString());
                        sendMess(true, user.getUid(), userID, mUri);

                        txtMessage.setText(""); mUri="";
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } else {
            ShowNotifyUser.dismissProgressDialog();
            mUri="";
        }
    }
    private String getFileExtention (Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }
}