package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.noface.StartActivity;
import com.example.noface.model.Acc;
import com.example.noface.model.Message;
import com.example.noface.model.Posts;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.R;
import com.example.noface.model.User;
import com.example.noface.service.ServiceAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFragment extends Fragment {
    private EditText edtEmail, edtPass, edtREpass;//,edtPhone;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String id;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String TAG = "firebase - REGISTER";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPass = view.findViewById(R.id.edtPass);
        edtREpass = view.findViewById(R.id.edtREpass);
        btnRegister = view.findViewById(R.id.btnRegister);
//        edtPhone = view.findViewById(R.id.edtPhone);


        //Su kien dang ky
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                String repass = edtREpass.getText().toString();
//                String phone = edtPhone.getText().toString();
                if (mail.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isEmailValid(mail)) {
                        Toast.makeText(getContext(), "Mail không đúng định dạng ", Toast.LENGTH_SHORT).show();


                    } else if (pass.length() < 8) {
                        Toast.makeText(getContext(), "Mật khẩu phải 8 ký tự trở lên", Toast.LENGTH_SHORT).show();
                    } else {
                        if (pass.equals(repass)) {
                            ShowNotifyUser.showProgressDialog(getContext(), "Vui lòng đợi");
                            // sendVerificationCode("+84" + edtPhone.getText().toString());
                            createAccount(mail,pass);

                        } else {
                            Toast.makeText(getContext(), "Mật khẩu xác nhận không trùng", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });
        return view;
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // số điện thoại cần xác thực
                        .setTimeout(60L, TimeUnit.SECONDS) //thời gian timeout
                        .setActivity(getActivity())
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
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
                            mAuth = FirebaseAuth.getInstance();
                            pushRealtime(mAuth.getCurrentUser());

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            id = mAuth.getCurrentUser().getUid();
                            String ava = "https://firebasestorage.googleapis.com/v0/b/noface-2e0d0.appspot.com/o/avatars%2Fuser.png?alt=media&token=2d9fd3dc-9a7d-4485-a501-9611e9f544aa";
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String strDate = sdf.format(c.getTime());
                            Acc acc = new Acc(id,"Ẩn Danh",ava,0,1,strDate);
                            Create(acc);
                            showDialog(user);

                        } else {
                            ShowNotifyUser.dismissProgressDialog();
                            Log.w(TAG, "Đăng ký thất bại", task.getException());
                            Toast.makeText(getContext(), "Email đã tồn tại!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showDialog(FirebaseUser user){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác thực Email");
        builder.setMessage("Đã gửi email xác thực \n Vui lòng kiểm tra trong Gmail của bạn");
        builder.setPositiveButton("Xác thực", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(getActivity(), "Nếu bạn đã xác thực, có thể đăng nhập để bắt đầu !!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        AlertDialog al = builder.create();
        al.show();
    }
    //API
    private void Create(Acc a) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.CreateUser(a)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Message message) {
        if (message.getStatus() == 1) {
            Toast.makeText(getContext(), "Đăng ký thành công nè", Toast.LENGTH_LONG).show();


        }
        if (message.getStatus() == 0)
            Toast.makeText(getContext(),  "Ủa ??", Toast.LENGTH_LONG).show();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.showAlertDialog(getContext(),"Lỗi: "+throwable+"");
    }

    ///xac thuc otp
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            //Hàm này được gọi trong hai trường hợp:
            //1. Trong một số trường hợp, điện thoại di động được xác minh tự động mà không cần mã xác minh.
            //2. Trên một số thiết bị, các dịch vụ của Google Play phát hiện SMS đến và thực hiện quy trình xác minh mà không cần người dùng thực hiện bất kỳ hành động nào.
            Log.d(TAG, "onVerificationCompleted:" + credential);

            //tự động điền mã OTP
//            edtNum1.setText(credential.getSmsCode().substring(0,1));
//            edtNum2.setText(credential.getSmsCode().substring(1,2));
//            edtNum3.setText(credential.getSmsCode().substring(2,3));
//            edtNum4.setText(credential.getSmsCode().substring(3,4));
//            edtNum5.setText(credential.getSmsCode().substring(4,5));
//            edtNum6.setText(credential.getSmsCode().substring(5,6));
//
//            verifyCode(credential.getSmsCode());
        }

        //fail
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);
            ShowNotifyUser.dismissProgressDialog();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                ShowNotifyUser.showAlertDialog(getContext(), "Request fail");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                ShowNotifyUser.showAlertDialog(getContext(), "Quota không đủ");
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent:" + verificationId);
            ShowNotifyUser.dismissProgressDialog();
            Toast.makeText(getContext(), "Đã gửi OTP", Toast.LENGTH_SHORT).show();
            showDialog();
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };
    private void verifyCode(String code) {
        ShowNotifyUser.showProgressDialog(getContext(), "Đang xác thực");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ShowNotifyUser.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            createAccount(edtEmail.getText().toString(),edtPass.getText().toString());
                            ShowNotifyUser.showAlertDialog(getContext(), "Thành công");
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                ShowNotifyUser.showAlertDialog(getContext(), "Lỗi");
                            }
                        }
                    }
                });
    }
    public void showDialog(){
        Button btnXacthuc,btnCancel;
        EditText edtOtp;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater1 = RegisterFragment.this.getLayoutInflater();
        View dialogView =  inflater1.inflate(R.layout.dialog_phoneverify,null);

        builder.setView(dialogView);
        builder.setTitle("Xác thực OTP");
        AlertDialog b = builder.create();
        b.show();
        btnXacthuc = dialogView.findViewById(R.id.btnXacthuc);
        btnCancel = dialogView.findViewById(R.id.btnCancel);
        edtOtp = dialogView.findViewById(R.id.edtOtp);
        btnXacthuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode(edtOtp.getText().toString());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.cancel();
            }
        });
    }
    public void pushRealtime(@NonNull FirebaseUser fUser){
        FirebaseDatabase   database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        User lUser = new User(fUser.getUid(),"Ẩn danh",fUser.getEmail(),"",false,"","offline");
        myRef.child(lUser.getIdUser()).setValue(lUser);
    }
}
