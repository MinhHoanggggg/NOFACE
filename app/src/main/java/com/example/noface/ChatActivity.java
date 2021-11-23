package com.example.noface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.Adapter.ListUserAdapter;
import com.example.noface.Adapter.MessageAdapter;
import com.example.noface.model.Chat;
import com.example.noface.model.User;
import com.example.noface.other.SetAvatar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView imgAvatarUser, btnSend;
    private TextView txtUserName;
    private EditText txtMessage;
    private RecyclerView rcvChat;
    MessageAdapter adapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String userID;
    List<Chat> lChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        imgAvatarUser = findViewById(R.id.imgAvatarUser);
        btnSend = findViewById(R.id.btnSend);
        txtUserName = findViewById(R.id.txtUserName);
        txtMessage = findViewById(R.id.txtMessage);
        rcvChat = findViewById(R.id.rcvChat);
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

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = txtMessage.getText().toString();
                if (!mess.equals("")){
                    sendMess(user.getUid(), userID, txtMessage.getText().toString());
                    txtMessage.setText("");
                }
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

    private void sendMess(String I, String you, String mess){
        DatabaseReference reference = firebaseDatabase.getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("from", I);
        hashMap.put("to", you);
        hashMap.put("message", mess);
        hashMap.put("seen", false);
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

}