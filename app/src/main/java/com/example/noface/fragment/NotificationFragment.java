package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.Adapter.NotificationAdapter;
import com.example.noface.PostActivity;
import com.example.noface.R;
import com.example.noface.model.Notification;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NotificationFragment extends Fragment {
    private NotificationAdapter notificationAdapter;
    private RecyclerView rclNoti;
    private ImageView noface;
    private TextView txtTb;
    private String token;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_notification, container, false);
        txtTb =view.findViewById(R.id.txtTb);
        rclNoti = view.findViewById(R.id.rclNoti);
        noface = view.findViewById(R.id.noface);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rclNoti.setLayoutManager(linearLayoutManager);
        //token
        DataToken dataToken = new DataToken(getContext());
        token = dataToken.getToken();
        ShowNotifyUser.showProgressDialog(getContext(),"Đang tải, đừng manh động...");
        GetNoti(token,user.getUid());

        return view;
    }
    private void GetNoti(String token,String idUser) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetNoti(token,idUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        Log.d("ERROR","Lỗi",throwable);
        Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleResponse(ArrayList<Notification> notifications) {
        //sort
        if(notifications.size()>0) {
            noface.setVisibility(View.GONE);
            notifications.sort(((o1, o2)
                    -> Integer.compare(o2.getID_Notification(), o1.getID_Notification())));

            notificationAdapter = new NotificationAdapter(getActivity(), notifications);
            ShowNotifyUser.dismissProgressDialog();
            rclNoti.setAdapter(notificationAdapter);
        }else {
            txtTb.setText("Bạn chưa có thông báo nào, hãy tương tác nhiều hơn nhé!!");
            rclNoti.setVisibility(getView().INVISIBLE);
            ShowNotifyUser.dismissProgressDialog();

        }
    }



}