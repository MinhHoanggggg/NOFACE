package com.example.noface.fragment;



import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.noface.MainActivity;
import com.example.noface.StartActivity;
import com.example.noface.model.Avatar;
import com.example.noface.model.Message;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.R;
import com.example.noface.model.User;
import com.example.noface.service.ServiceAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {
    private static final int MY_REQUEST_CODE = 10;
    MainActivity mainActivity ;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText edtName,edtPhone,edtEmail;
    private Button  btnUpdate;
    private ImageView imgAva,imgMedal;
    private Uri photoUri;
    private User lUser;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtName = view.findViewById(R.id.edtName);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        imgAva = view.findViewById(R.id.imgAva);
        imgMedal = view.findViewById(R.id.imgMedal);
        //Get token
        DataToken dataToken = new DataToken(getContext());
        token = dataToken.getToken();
        mainActivity = (MainActivity) getActivity();

        setUI(user);

//


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowNotifyUser.showProgressDialog(getContext(),"??ang t???i..");
                String username = edtName.getText().toString();
                if(username.isEmpty()){
                    username ="???n danh";
                }
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .setPhotoUri(photoUri)
                        .build();


                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ShowNotifyUser.dismissProgressDialog();
                                    ///cap nhat thong tin realtime
                                    mainActivity.changeHeader(user);
                                    if(user.isEmailVerified()){
                                        lUser.setMailChecked(true);
                                    }
                                    else
                                        lUser.setMailChecked(false);
                                    String username =edtName.getText().toString();
                                    if(username.isEmpty()){
                                        username ="???n danh";
                                    }
                                    lUser.setName(username);
                                    lUser.setPhone(edtPhone.getText().toString());
                                    pushRealtime(lUser);
                                    //Add Ava
                                    Avatar ava = new Avatar(lUser.getIdUser(), lUser.getAvaPath(),username);
                                    addAva(ava);
                                    Toast.makeText(getContext(), "C???p nh???t th??nh c??ng", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                DatabaseReference myRef = firebaseDatabase.getReference();
                myRef.child("Users/" + user.getUid() + "/name").setValue(username);
            }
        });
        imgAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // onClickRequestPermission();
                DialogFragmentAvatar dialogFragment = new DialogFragmentAvatar();

                dialogFragment.show(getChildFragmentManager(),"DialogFragmentAvatar");
            }
        });
        return view ;
    }
    private void onClickRequestPermission(){

        //neu phien ban dien thoai <23
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mainActivity.openGallery();
            return;
        }
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mainActivity.openGallery();
        }else{
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
        }
    }




 public void setBitMapImgView(Bitmap bitmap){
        imgAva.setImageBitmap(bitmap);
 }

 public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

   ////
 private void setUI(FirebaseUser user){

//         Uri photoUrl = user.getPhotoUrl();
//         Glide.with(this.getActivity()).load(photoUrl).error(R.drawable.ic_user).into(imgAva);
     //

        String refName = user.getUid();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lUser = snapshot.getValue(User.class);
                if (!lUser.getName().isEmpty()){
                    edtName.setText(lUser.getName());
                }
                if (!lUser.getPhone().isEmpty()){
                    edtPhone.setText(lUser.getPhone());
                }
                edtEmail.setText(lUser.getEmail());

                if(lUser.getAvaPath()!=null){
                    setAva(lUser.getAvaPath());
                }
                else
                    imgAva.setImageResource(R.drawable.ic_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void pushRealtime(User lUser){
        FirebaseDatabase   database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(lUser.getIdUser());
        myRef.setValue(lUser);
    }
    public void setAva(String avaPath){
        if (getActivity() == null) {
            return;
        }
        lUser.setAvaPath(avaPath);
        if(!avaPath.isEmpty()){
            Glide.with(getActivity()).load(avaPath).into(imgAva);
        }

    }
    private void addAva( Avatar avatar) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.AddAva(token,avatar)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1, this::handleError)
        );
    }

    private void handleResponse1(Message message) {
        if(message.getStatus()!=1){
            message.getStatus();
        }
    }
    private void handleError(Throwable throwable) {
        Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
    }

}
