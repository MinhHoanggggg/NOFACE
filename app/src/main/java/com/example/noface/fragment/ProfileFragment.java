package com.example.noface.fragment;



import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.noface.DialogFragmentAvatar;
import com.example.noface.MainActivity;
import com.example.noface.Others.ShowNotifyUser;
import com.example.noface.R;
import com.example.noface.model.User;
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

public class ProfileFragment extends Fragment {
    private static final int MY_REQUEST_CODE = 10;
    MainActivity mainActivity ;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText edtName,edtPhone,edtEmail;
    private Button  btnUpdate;
    private ImageView imgAva;
    private Uri photoUri;
    private User lUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtName = view.findViewById(R.id.edtName);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        imgAva = view.findViewById(R.id.imgAva);

        mainActivity = (MainActivity) getActivity();
        setUI(user);

//


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowNotifyUser.showProgressDialog(getContext(),"Đang tải..");
                String username = edtName.getText().toString();
                if(username.isEmpty()){
                    username ="Ẩn danh";
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
                                    if(user.isEmailVerified()){
                                        lUser.setMailChecked(true);
                                    }
                                    else
                                        lUser.setMailChecked(false);
                                    String username =edtName.getText().toString();
                                    if(username.isEmpty()){
                                        username ="Ẩn danh";
                                    }
                                    lUser.setName(username);
                                    lUser.setPhone(edtPhone.getText().toString());
                                    pushRealtime(lUser);
                                    //
                                mainActivity.changeHeader(user);
                                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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



 private void showDialog(){
     AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
     builder.setTitle("Xác thực Email");
     builder.setMessage("Email của bạn chưa được xác thưc, vui lòng kiểm tra");
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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid().toString();
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
                if (!lUser.isMailChecked()){
                    showDialog();
                }
                if(lUser.getAvaPath()!=null){
                    switch (lUser.getAvaPath()){
                        case "ava1":
                            imgAva.setImageResource(R.drawable.ava1);
                            break;
                        case "ava2":
                            imgAva.setImageResource(R.drawable.ava2);
                            break;
                        case "ava3":
                            imgAva.setImageResource(R.drawable.ava3);
                            break;
                        case "ava4":
                            imgAva.setImageResource(R.drawable.ava4);
                            break;
                        case "ava5":
                            imgAva.setImageResource(R.drawable.ava5);
                            break;
                        case "ava6":
                            imgAva.setImageResource(R.drawable.ava6);
                            break;
                        case "ava7":
                            imgAva.setImageResource(R.drawable.ava7);
                            break;
                        case "ava8":
                            imgAva.setImageResource(R.drawable.ava8);
                            break;
                        case "ava9":
                            imgAva.setImageResource(R.drawable.ava9);
                            break;
                        case "ava10":
                            imgAva.setImageResource(R.drawable.ava10);
                            break;
                        default: break;
                    }
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
        lUser.setAvaPath(avaPath);
        if(!avaPath.isEmpty()){
            switch (avaPath){
                case "ava1":
                    imgAva.setImageResource(R.drawable.ava1);
                    break;
                case "ava2":
                    imgAva.setImageResource(R.drawable.ava2);
                    break;
                case "ava3":
                    imgAva.setImageResource(R.drawable.ava3);
                    break;
                case "ava4":
                    imgAva.setImageResource(R.drawable.ava4);
                    break;
                case "ava5":
                    imgAva.setImageResource(R.drawable.ava5);
                    break;
                case "ava6":
                    imgAva.setImageResource(R.drawable.ava6);
                    break;
                case "ava7":
                    imgAva.setImageResource(R.drawable.ava7);
                    break;
                case "ava8":
                    imgAva.setImageResource(R.drawable.ava8);
                    break;
                case "ava9":
                    imgAva.setImageResource(R.drawable.ava9);
                    break;
                case "ava10":
                    imgAva.setImageResource(R.drawable.ava10);
                    break;
                default: break;
            }
        }

    }
}
