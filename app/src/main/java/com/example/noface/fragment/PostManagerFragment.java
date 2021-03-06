package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.PostAdapter;

import com.example.noface.R;
import com.example.noface.model.Achievements;
import com.example.noface.model.Medals;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
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
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostManagerFragment extends Fragment {
    private ImageView imgMedal;
    private RecyclerView rcv_posts;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User lUser;
    private ImageView img_mng_Ava;
    private TextView txtName;
    String token;
    public ArrayList<Topic> lstTopic = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_posts_manager, container, false);

        rcv_posts = view.findViewById(R.id.rcv_posts);
        img_mng_Ava = view.findViewById(R.id.img_mng_Ava);
        txtName = view.findViewById(R.id.txtName);
        imgMedal = view.findViewById(R.id.imgMedal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(linearLayoutManager);

        ShowNotifyUser.showProgressDialog(getContext(), "??ang t???i, ?????ng manh ?????ng...");
        //token
        DataToken dataToken = new DataToken(getContext());
        token = dataToken.getToken();
        //API data
        String userid = user.getUid();
        Wall(userid);
        setUI(user); //Set UI
        rcv_posts.setHasFixedSize(true); //cu???n nu???t h??n
        GetAllTopic();
        //Xem Medal
        imgMedal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentMedal dialogFragment = new DialogFragmentMedal();
                dialogFragment.id = user.getUid();
                dialogFragment.show(getChildFragmentManager(),"DialogFragmentMedal");
            }
        });
        return view;
    }

    //    get data t??? API
    private void Wall(String userid) {
        DataToken dataToken = new DataToken(getContext());
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.Wall(dataToken.getToken(), userid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(ArrayList<Posts> posts) {
        try {
            GetAllTopic();
            PostAdapter postAdapter = new PostAdapter(posts,lstTopic, getContext());
            rcv_posts.setAdapter(postAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ShowNotifyUser.dismissProgressDialog();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(getContext(), "Kh??ng ???n r???i ?????i v????ng ??i! ???? c?? l???i x???y ra");
    }


    private void setUI(FirebaseUser user) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lUser = snapshot.getValue(User.class);
                if (!lUser.getName().isEmpty()) {
                    int len = user.getUid().length();
                    txtName.setText(lUser.getName()+" #"+user.getUid().substring(len-4, len));
                }
                if (getActivity() == null) {
                    return;
                } else
                    SetAvatar.SetAva(img_mng_Ava, lUser.getAvaPath());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void GetAllTopic() {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetAllTopic(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ArrayList<Topic> topics) {
        for(int i =0; i<topics.size();i++ ){
            lstTopic.add(topics.get(i));
        }
    }

}