package com.example.noface.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.noface.Adapter.ListUserAdapter;
import com.example.noface.R;
import com.example.noface.model.User;
import com.example.noface.other.ShowNotifyUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private SearchView searchView;
    ArrayList<User> arrayList;
    ListUserAdapter adapter;
    String myID = user.getUid(), frID= "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_chat, container, false);
        searchView = view.findViewById(R.id.searchView);
        rcv_listChat = view.findViewById(R.id.rcv_listChat);
        rcv_listChat.setHasFixedSize(true);
        rcv_listChat.setLayoutManager(new LinearLayoutManager(getContext()));
        arrayList = new ArrayList<>();

        String myID = user.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User users = dataSnapshot.getValue(User.class);
                    if (!users.getIdUser().equals(myID)){
                        arrayList.add(users);
                    }
                }
                adapter= new ListUserAdapter(getContext(), arrayList);
                rcv_listChat.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }
}