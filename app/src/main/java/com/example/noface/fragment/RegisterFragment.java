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
                    Toast.makeText(getContext(), "Vui l??ng nh???p ?????y ????? th??ng tin!", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isEmailValid(mail)) {
                        Toast.makeText(getContext(), "Mail kh??ng ????ng ?????nh d???ng ", Toast.LENGTH_SHORT).show();


                    } else if (pass.length() < 8) {
                        Toast.makeText(getContext(), "M???t kh???u ph???i 8 k?? t??? tr??? l??n", Toast.LENGTH_SHORT).show();
                    } else {
                        if (pass.equals(repass)) {
                            ShowNotifyUser.showProgressDialog(getContext(), "Vui l??ng ?????i");
                            // sendVerificationCode("+84" + edtPhone.getText().toString());
                            createAccount(mail,pass);

                        } else {
                            Toast.makeText(getContext(), "M???t kh???u x??c nh???n kh??ng tr??ng", Toast.LENGTH_SHORT).show();
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
                        .setPhoneNumber(number)       // s??? ??i???n tho???i c???n x??c th???c
                        .setTimeout(60L, TimeUnit.SECONDS) //th???i gian timeout
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
                            Acc acc = new Acc(id,"???n Danh",ava,0,1,strDate);
                            Create(acc);
                            showDialog(user);

                        } else {
                            ShowNotifyUser.dismissProgressDialog();
                            Log.w(TAG, "????ng k?? th???t b???i", task.getException());
                            Toast.makeText(getContext(), "Email ???? t???n t???i!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showDialog(FirebaseUser user){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("X??c th???c Email");
        builder.setMessage("???? g???i email x??c th???c \n Vui l??ng ki???m tra trong Gmail c???a b???n");
        builder.setPositiveButton("X??c th???c", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(getActivity(), "N???u b???n ???? x??c th???c, c?? th??? ????ng nh???p ????? b???t ?????u !!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "????ng k?? th??nh c??ng n??", Toast.LENGTH_LONG).show();


        }
        if (message.getStatus() == 0)
            Toast.makeText(getContext(),  "???a ??", Toast.LENGTH_LONG).show();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.showAlertDialog(getContext(),"L???i: "+throwable+"");
    }

    ///xac thuc otp
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            //H??m n??y ???????c g???i trong hai tr?????ng h???p:
            //1. Trong m???t s??? tr?????ng h???p, ??i???n tho???i di ?????ng ???????c x??c minh t??? ?????ng m?? kh??ng c???n m?? x??c minh.
            //2. Tr??n m???t s??? thi???t b???, c??c d???ch v??? c???a Google Play ph??t hi???n SMS ?????n v?? th???c hi???n quy tr??nh x??c minh m?? kh??ng c???n ng?????i d??ng th???c hi???n b???t k??? h??nh ?????ng n??o.
            Log.d(TAG, "onVerificationCompleted:" + credential);

            //t??? ?????ng ??i???n m?? OTP
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
                ShowNotifyUser.showAlertDialog(getContext(), "Quota kh??ng ?????");
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent:" + verificationId);
            ShowNotifyUser.dismissProgressDialog();
            Toast.makeText(getContext(), "???? g???i OTP", Toast.LENGTH_SHORT).show();
            showDialog();
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };
    private void verifyCode(String code) {
        ShowNotifyUser.showProgressDialog(getContext(), "??ang x??c th???c");
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
                            ShowNotifyUser.showAlertDialog(getContext(), "Th??nh c??ng");
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                ShowNotifyUser.showAlertDialog(getContext(), "L???i");
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
        builder.setTitle("X??c th???c OTP");
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
        User lUser = new User(fUser.getUid(),"???n danh",fUser.getEmail(),"",false,"","offline");
        myRef.child(lUser.getIdUser()).setValue(lUser);
    }
}
