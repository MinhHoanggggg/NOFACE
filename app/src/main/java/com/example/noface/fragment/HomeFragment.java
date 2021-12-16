package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.noface.Adapter.HomeAdapter;
import com.example.noface.Adapter.PostAdapter;
import com.example.noface.CreatePost;
import com.example.noface.LoginActivity;
import com.example.noface.MainActivity;
import com.example.noface.R;
import com.example.noface.StartActivity;
import com.example.noface.model.Posts;
import com.example.noface.model.Topic;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private RecyclerView rcv_posts;
    private ImageButton btn_create;
    String token;
    public ArrayList<Topic> lstTopic = new ArrayList<>();
    Parcelable state;
    private LinearLayoutManager mLayoutManager;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);

        rcv_posts = view.findViewById(R.id.rcv_posts);
        btn_create = view.findViewById(R.id.btn_create);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcv_posts.setLayoutManager(mLayoutManager);
        mLayoutManager.onRestoreInstanceState(state);

        ShowNotifyUser.showProgressDialog(getContext(),"Đang tải, đừng manh động...");
        //token
        DataToken dataToken = new DataToken(getContext());
        token = dataToken.getToken();
        PostTrending();
        GetAllTopic();
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.isEmailVerified())
                startActivity(new Intent(getContext(), CreatePost.class));
                else
                    showDialog();

            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        state = mLayoutManager.onSaveInstanceState();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 2){
            assert data != null;
            int result = data.getIntExtra("result", 0);
            if (result == 1){
                PostTrending();
            }
        }
    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác thực Email");
        builder.setMessage("Vui lòng xác thực Email để thực hiện tác vụ này");
        builder.setPositiveButton("Xác thực", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Đã gửi email xác thực \n Vui lòng kiểm tra trong Gmail của bạn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        AlertDialog al = builder.create();
        al.show();
    }

    //    get data từ API
    private void PostTrending() {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.home(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(ArrayList<Posts> posts) {
        try {


            PostAdapter postAdapter = new PostAdapter(posts,lstTopic,getContext());
            rcv_posts.setAdapter(postAdapter);
            mLayoutManager.onRestoreInstanceState(state);

        }catch (Exception e){
            e.printStackTrace();
        }
        ShowNotifyUser.dismissProgressDialog();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
//        ShowNotifyUser.showAlertDialog(getContext(),"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), StartActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        Toast.makeText(getContext(), "Đăng xuất tài khoản", Toast.LENGTH_SHORT).show();
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


