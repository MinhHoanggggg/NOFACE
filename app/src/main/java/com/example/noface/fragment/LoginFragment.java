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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.noface.MainActivity;
import com.example.noface.StartActivity;
import com.example.noface.model.Token;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.R;
import com.example.noface.ResetPass;
import com.example.noface.service.ServiceAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment {
    private Button btnLogin;
    private TextView txtForPass;
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
        txtForPass = view.findViewById(R.id.txtForPass);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                if(mail.isEmpty()||pass.isEmpty()){
                    Toast.makeText(getContext(), "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }else {

                        ShowNotifyUser.showProgressDialog(getContext(), "Đang tải, đừng manh động...");
                        loginUser(mail, pass);


                }
            }
        });
        txtForPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ResetPass.class));
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
//                            ShowNotifyUser.dismissProgressDialog();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()){
                            Toast.makeText(getContext(), "Đăng nhập thành công",
                                    Toast.LENGTH_SHORT).show();

                            assert user != null;
                            String id = user.getUid();
                            Login(id);
                            }else
                                showDialog(user);

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
        DataToken dataToken = new DataToken(getContext());
        dataToken.saveToken(token.getToken(), token.getRefreshToken());
        ShowNotifyUser.dismissProgressDialog();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }


    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(getContext(),"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }


}
