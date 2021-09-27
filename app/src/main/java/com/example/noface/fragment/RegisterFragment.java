package com.example.noface.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.noface.LoginActivity;
import com.example.noface.Others.ShowNotifyUser;
import com.example.noface.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {
    private EditText edtEmail,edtPass,edtREpass;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    private String TAG = "firebase - REGISTER";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.layout_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPass = view.findViewById(R.id.edtPass);
        edtREpass = view.findViewById(R.id.edtREpass);
        btnRegister = view.findViewById(R.id.btnRegister);


        //Su kien dang ky
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                String repass = edtREpass.getText().toString();
                if(mail.isEmpty()|| pass.isEmpty()|| repass.isEmpty()){
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!isEmailValid(mail) ){
                        Toast.makeText(getContext(), "Mail không đúng định dạng ", Toast.LENGTH_SHORT).show();


                    }else if(pass.length() < 8){
                        Toast.makeText(getContext(),"Mật khẩu phải 8 ký tự trở lên", Toast.LENGTH_SHORT).show();
                    }else {
                        if(pass.equals(repass)){
                            createAccount(mail,pass);
                            ShowNotifyUser.showProgressDialog(getContext(),"Đang tải vui lòng chờ");
                        }
                        else {
                            Toast.makeText(getContext(), "Mật khẩu xác nhận không trùng", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
        return view;
    }
    // check mail hop le
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    ///tao tai khoan
    private void createAccount(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
               .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           ShowNotifyUser.dismissProgressDialog();
                           Log.d(TAG, "Đăng ký thành công");
                           FirebaseUser user = mAuth.getCurrentUser();
                           Toast.makeText(getContext(), "Đăng ký thành công",
                                   Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(getActivity(), LoginActivity.class));

                       } else {
                           ShowNotifyUser.dismissProgressDialog();
                           Log.w(TAG, "Đăng ký thất bại", task.getException());
                           Toast.makeText(getContext(), "Đăng ký thất bại",
                                   Toast.LENGTH_SHORT).show();
                       }
                   }
               });
    }
///xac thuc mail
    private void verifyMail( ) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

}
