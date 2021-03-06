package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noface.fragment.RegisterFragment;
import com.example.noface.model.Acc;
import com.example.noface.model.Message;
import com.example.noface.model.Token;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
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
import com.hbb20.CountryCodePicker;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginByPhone extends AppCompatActivity {
    private CountryCodePicker pkCode;
    private EditText edtPhoneNum;
    private Button btnNext;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_phone);
        pkCode = findViewById(R.id.pkCode);
        edtPhoneNum = findViewById(R.id.edtPhoneNum);
        btnNext = findViewById(R.id.btnNext);
        mAuth = FirebaseAuth.getInstance();
        btnNext.setOnClickListener(View ->{
           String phoneNum = "+"+pkCode.getSelectedCountryCode()+edtPhoneNum.getText().toString();
            sendVerificationCode(phoneNum);
        });

    }
    private void sendVerificationCode(String number) {
        ShowNotifyUser.showProgressDialog(this,"??ang t???i ch??? x??u nho!");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // s??? ??i???n tho???i c???n x??c th???c
                        .setTimeout(60L, TimeUnit.SECONDS) //th???i gian timeout
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

   private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            //H??m n??y ???????c g???i trong hai tr?????ng h???p:
            //1. Trong m???t s??? tr?????ng h???p, ??i???n tho???i di ?????ng ???????c x??c minh t??? ?????ng m?? kh??ng c???n m?? x??c minh.
            //2. Tr??n m???t s??? thi???t b???, c??c d???ch v??? c???a Google Play ph??t hi???n SMS ?????n v?? th???c hi???n quy tr??nh x??c minh m?? kh??ng c???n ng?????i d??ng th???c hi???n b???t k??? h??nh ?????ng n??o.
//            Log.d(TAG, "onVerificationCompleted:" + credential);

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
//            Log.w(TAG, "onVerificationFailed", e);
            ShowNotifyUser.dismissProgressDialog();
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                ShowNotifyUser.showAlertDialog(LoginByPhone.this, "Request fail");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                ShowNotifyUser.showAlertDialog(LoginByPhone.this, "Quota kh??ng ?????");
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
//            Log.d(TAG, "onCodeSent:" + verificationId);
            ShowNotifyUser.dismissProgressDialog();
            Toast.makeText(getApplicationContext(), "???? g???i OTP", Toast.LENGTH_SHORT).show();
            showDialog();
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };
    private void verifyCode(String code) {
        ShowNotifyUser.showProgressDialog(LoginByPhone.this, "??ang x??c th???c");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ShowNotifyUser.dismissProgressDialog();
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            String id = user.getUid();
                            pushRealtime(user);
                            Toast.makeText(getApplicationContext(), "Th??nh c??ng", Toast.LENGTH_SHORT).show();
                            String ava = "https://firebasestorage.googleapis.com/v0/b/noface-2e0d0.appspot.com/o/avatars%2Fuser.png?alt=media&token=2d9fd3dc-9a7d-4485-a501-9611e9f544aa";
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                           String strDate = sdf.format(c.getTime());
                            Create(new Acc(user.getUid(),"???n danh",ava,0,1,strDate));
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "L???i g??i men", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    public void showDialog(){
        Button btnXacthuc,btnCancel;
        EditText edtOtp;
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginByPhone.this);
        LayoutInflater inflater1 = LoginByPhone.this.getLayoutInflater();
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        User lUser = new User(fUser.getUid(),"???n danh","",fUser.getPhoneNumber(),true,"","offline");
        myRef.child(lUser.getIdUser()).setValue(lUser);
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
            Toast.makeText(getApplicationContext(), "Th??nh c??ng n??", Toast.LENGTH_LONG).show();
            Login(mAuth.getUid());
        }
        if (message.getStatus() == 0)
            Toast.makeText(getApplicationContext(),  "???a ??", Toast.LENGTH_LONG).show();
    }
    private void Login(String idUser) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetToken(idUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(Token token) {
        DataToken dataToken = new DataToken(LoginByPhone.this);
        dataToken.saveToken(token.getToken(), token.getRefreshToken());
        startActivity(new Intent(LoginByPhone.this, MainActivity.class));
        finish();
    }



    private void handleError(Throwable throwable) {
        ShowNotifyUser.showAlertDialog(LoginByPhone.this,"Kh??ng ???n r???i ?????i v????ng ??i! ???? c?? l???i x???y ra n??");
    }
}