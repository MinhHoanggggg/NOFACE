package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.AvatarAdapter;
import com.example.noface.Adapter.MedalAdapter;
import com.example.noface.R;
import com.example.noface.model.Achievements;
import com.example.noface.model.Ava;
import com.example.noface.model.Medals;
import com.example.noface.model.Token;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
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


public class DialogFragmentMedal extends DialogFragment {
    private TextView txtTitle;
    private RecyclerView rclMedal;
    private ArrayList<Medals> lstMedal = new ArrayList<>();
    private MedalAdapter medalAdapter;
    private String token;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private User lUser;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public String id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_dialog_medal, container, false);
        rclMedal = view.findViewById(R.id.rclMedal);
        txtTitle = view.findViewById(R.id.txtTitle);

        //Get token
        DataToken dataToken = new DataToken(getContext());
        token = dataToken.getToken();
        GetMedals(id);

//        LinearLayoutManager linearLayoutManager =
//                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        rclAva.setLayoutManager(linearLayoutManager);
//        rclAva.setAdapter(avatarAdapter);
        return view;
    }
    public void GetMedals(String id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetMedals(token,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseMedal, this::handleError)
        );
    }

    private void handleError(Throwable throwable) {
    }


    private void handleResponseMedal(List<Achievements> achievements) {
        //oke
        String im = "";
        if (achievements.size()<1){
            if(user.getUid().equals(id.trim())){
                im = "Bạn";
            }
            else{
                im = user.getDisplayName();
            }
            txtTitle.setText(im+" không có danh hiệu nào");
        }else{
        for(int i=0;i<achievements.size();i++){
            Medals md = achievements.get(i).getMedals();
            lstMedal.add(md);
        }
            if(user.getUid().equals(id.trim())){
                im = "Bạn";
                txtTitle.setText(im+" có "+lstMedal.size()+" danh hiệu");
            }
            else{
                getName(id);
            }


            medalAdapter = new MedalAdapter(getContext(), lstMedal, new MedalAdapter.MedalAdapterListener() {
                @Override
                public void click(View v, int position) {
                    Toast.makeText(getActivity(), lstMedal.get(position).getDescription(), Toast.LENGTH_SHORT).show();
                }
            });
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            rclMedal.setLayoutManager(gridLayoutManager);
            rclMedal.setAdapter(medalAdapter);
        }

    }
    private void getName(String id){

//         Uri photoUrl = user.getPhotoUrl();
//         Glide.with(this.getActivity()).load(photoUrl).error(R.drawable.ic_user).into(imgAva);
        //

        String refName = id.trim();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lUser = snapshot.getValue(User.class);
                if (!lUser.getName().isEmpty()){
                    txtTitle.setText(lUser.getName()+" có "+lstMedal.size()+" danh hiệu");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}