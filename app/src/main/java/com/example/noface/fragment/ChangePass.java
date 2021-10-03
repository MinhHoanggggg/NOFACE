package com.example.noface.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noface.Others.ShowNotifyUser;
import com.example.noface.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePass extends Fragment {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Button btnChange;
    private EditText edtPass,edtREpass,edtNewpass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_change_pass, container, false);
        edtNewpass = view.findViewById(R.id.edtNewpass);
        edtPass = view.findViewById(R.id.edtPass);
        edtREpass = view.findViewById(R.id.edtREpass);
        btnChange = view.findViewById(R.id.btnChange);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = edtPass.getText().toString();
                String newPass = edtNewpass.getText().toString();
                String rePass = edtREpass.getText().toString();
                if(pass.isEmpty()||newPass.isEmpty()||rePass.isEmpty()){
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                }else if(!newPass.equals(rePass)){
                    Toast.makeText(getContext(), "Mật khẩu xác thực chưa chính xác ", Toast.LENGTH_SHORT).show();
                }else {
                    verAccount(user.getEmail(),pass,newPass);
                    ShowNotifyUser.showProgressDialog(getContext(),"Đang tải..");
                }
            }
        });
        return view;
    }
    private void verAccount(String mail, String pass,String newPass){
        AuthCredential credential = EmailAuthProvider
                .getCredential(mail, pass);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        changePass(newPass);
                    }
                });
    }
    private void changePass(String pass){
        String newPassword = pass;

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ShowNotifyUser.dismissProgressDialog();
                            Toast.makeText(getContext(), "User password updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}