package com.example.noface.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.noface.Adapter.ListUserAdapter;
import com.example.noface.R;
import com.example.noface.model.Chat;
import com.example.noface.model.User;
import com.example.noface.other.ShowNotifyUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainChatFragment extends Fragment {
    private FirebaseAuth user = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private RecyclerView rcv_listChat;
    ArrayList<String> arrayList;
    private ImageView noface;
    ArrayList<User> lUser;
    ListUserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_chat, container, false);
        rcv_listChat = view.findViewById(R.id.rcv_listChat);
        noface = view.findViewById(R.id.noface);
        ShowNotifyUser.showProgressDialog(getActivity(), "Đang tải, đừng manh động...");
        rcv_listChat.setHasFixedSize(true);
        rcv_listChat.setLayoutManager(new LinearLayoutManager(getContext()));
        arrayList = new ArrayList<>();

        String myID = user.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("Chat");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getFrom().equals(myID)) {
                        arrayList.add(chat.getTo());
                    }
                    if (chat.getTo().equals(myID)) {
                        arrayList.add(chat.getFrom());
                    }
                } //lấy id để get data user

                ShowNotifyUser.dismissProgressDialog();
                RemoveDuplicate(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

    private void  RemoveDuplicate(ArrayList<String> arrayList) {
        ArrayList<String> arr = new ArrayList<>();
        for (String element : arrayList) {
            if (!arr.contains(element)) {
                arr.add(element);
            }
        }
        readChatUser(arr);
    }

    private void readChatUser(ArrayList<String> list) {
        lUser = new ArrayList<>();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for (String i : list)
                        if (user.getIdUser().equals(i))
                            try {
                                if (lUser.size() != 0) {
                                    for (User a : lUser)
                                        if (!user.getIdUser().equals(a.getIdUser()))
                                            lUser.add(user);
                                } else {
                                    lUser.add(user);
                                }
                            }catch (Throwable e){

                            }
                }

                adapter = new ListUserAdapter(getContext(), lUser, true);
                rcv_listChat.setAdapter(adapter);

                if (arrayList.size() == 0) noface.setVisibility(View.VISIBLE);
                else noface.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}