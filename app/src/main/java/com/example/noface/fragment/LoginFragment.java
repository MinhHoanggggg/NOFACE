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

import com.example.noface.MainActivity;
import com.example.noface.Others.ShowNotifyUser;
import com.example.noface.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private Button btnLogin;
    private EditText edtEmail,edtPass;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "firebase - LOGIN";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.layout_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = view.findViewById(R.id.btnLogin);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPass = view.findViewById(R.id.edtPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                if(mail.isEmpty()||pass.isEmpty()){
                    Toast.makeText(getContext(), "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }else {
                    ShowNotifyUser.showProgressDialog(getContext(),"Đang tải...");
                    loginUser(mail,pass);
                }
            }
        });
        return view;
    }
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            ShowNotifyUser.dismissProgressDialog();
                            Log.w(TAG, "đăng nhập thất bại", task.getException());
                            Toast.makeText(getContext(), "Đăng nhập thất bại",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ShowNotifyUser.dismissProgressDialog();
                            Toast.makeText(getContext(), "Đăng nhập thành công",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                });
    }

}
