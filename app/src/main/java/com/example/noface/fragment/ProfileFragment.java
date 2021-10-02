package com.example.noface.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noface.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileFragment extends Fragment {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText edtName,edtPhone,edtEmail;
    private Button  btnUpdate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtName = view.findViewById(R.id.edtName);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        if(!user.isEmailVerified())
                showDialog();
        edtEmail.setText(user.getEmail());
        edtName.setText(user.getDisplayName());
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edtName.getText().toString())

                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        return view ;
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
                                 Toast.makeText(getContext(), "Đã gửi email xác thực", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
         }
     });
 }
}
