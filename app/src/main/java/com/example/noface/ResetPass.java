package com.example.noface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noface.other.ShowNotifyUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetPass extends AppCompatActivity {
    private EditText edtEmail;

    private Button btnResPass, btnBack;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ShowNotifyUser showNotifyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status("online");
        setContentView(R.layout.activity_reset_pass);
        edtEmail = findViewById(R.id.edtEmail);
        btnResPass = findViewById(R.id.btnResPass);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnResPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotifyUser.showProgressDialog(ResetPass.this, "Đang tải..");
                sendMail();

            }
        });

    }

    private void sendMail() {
        showNotifyUser.dismissProgressDialog();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = edtEmail.getText().toString();
        if (!emailAddress.isEmpty()) {
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Đã gửi mail xác nhận, vui lòng kiểm tra ", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {

                                Toast.makeText(getApplicationContext(), "Gửi mail thất bại, vui lòng kiểm tra địa chỉ email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else
            Toast.makeText(getApplicationContext(), "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
    }



    private void status(String status) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        myRef.child("Users/" + user.getUid() + "/status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}