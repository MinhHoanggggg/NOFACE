package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.ListUserAdapter;
import com.example.noface.R;
import com.example.noface.model.Friend;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
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

public class FriendFragment extends Fragment {
    private FirebaseAuth user = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private RecyclerView rcv_listChat;
    private ImageView noface;
    ArrayList<User> arrayList;
    ListUserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main_chat, container, false);
        rcv_listChat = view.findViewById(R.id.rcv_listChat);
        noface = view.findViewById(R.id.noface);

        rcv_listChat.setHasFixedSize(true);
        rcv_listChat.setLayoutManager(new LinearLayoutManager(getContext()));
        arrayList = new ArrayList<>();

        ShowNotifyUser.showProgressDialog(getContext(), "Đang tải, đừng manh động...");

        DataToken dataToken = new DataToken(getContext());
        String token = dataToken.getToken();

        listFr(token, user.getUid());


        return view;
    }

    private void listFr(String token, String id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.listFriend(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ArrayList<Friend> friend) {
        getUser(friend);
        ShowNotifyUser.dismissProgressDialog();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
    }

    public void getUser(ArrayList<Friend> friend) {
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User users = dataSnapshot.getValue(User.class);
                    for (int i = 0; i < friend.size(); i++) {
                        if (friend.get(i).getIDUser().trim().equals(user.getUid())){
                            if (users.getIdUser().equals(friend.get(i).getIDFriends().trim())) {
                                arrayList.add(users);
                                break;
                            }
                        } else {
                            if (users.getIdUser().equals(friend.get(i).getIDUser().trim())) {
                                arrayList.add(users);
                                break;
                            }
                        }

                    }
                }

                adapter = new ListUserAdapter(getContext(), arrayList, false);
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