package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.model.Acc;
import com.example.noface.model.Message;
import com.example.noface.model.Token;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class StartActivity extends AppCompatActivity {
    private Button btnCreateUser,btnPhone;
    private LoginButton btnFacebook;
    private SignInButton btnGoogle;
    private TextView txtLogin;
    private static final String TAG = "GoogleActivity";
    private static final String TAG1 = "FacebookAuthentication";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final int RC_SIGN_IN = 123;


    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //đã login vào luôn
        if(user != null){
            String id = user.getUid();
            Login(id);
        }

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_start);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnPhone = findViewById(R.id.btnPhone);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        txtLogin = findViewById(R.id.txtLogin);
        mAuth =  FirebaseAuth.getInstance();
        //google
        request();
        //fb
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG1, "onSuccess" +loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: "+error);
            }
        });
        btnFacebook.setReadPermissions("email", "public_profile");
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mAuth.signOut();
                }
            }
        };

       btnGoogle.setOnClickListener(view ->{
           signIn();
       });
       btnCreateUser.setOnClickListener(view ->{
           startActivity(new Intent(StartActivity.this,LoginActivity.class));
       });
       txtLogin.setOnClickListener(view ->{
           startActivity(new Intent(StartActivity.this,LoginActivity.class));
       });
       btnPhone.setOnClickListener(view ->{
           startActivity(new Intent(StartActivity.this,LoginByPhone.class));
       });

    }

    //Start Google
    private void request(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getApplicationContext(), "firebaseAuthWithGoogle:" + account.getId(), Toast.LENGTH_SHORT).show();

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();

            }
        }
        else{
            mCallbackManager.onActivityResult(requestCode,resultCode,data);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Thành công rồi nha đại ca!", Toast.LENGTH_SHORT).show();

                            pushRealtime(user);
                            String ava = "https://firebasestorage.googleapis.com/v0/b/noface-2e0d0.appspot.com/o/avatars%2Fuser.png?alt=media&token=2d9fd3dc-9a7d-4485-a501-9611e9f544aa";
                            Create(new Acc(user.getUid(),"Ẩn Danh",ava,0,1));
                            

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Lỗi rồi đại vương ơi!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    ///End Google

    public void pushRealtime(@NonNull FirebaseUser fUser){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        User lUser = new User(fUser.getUid(),"Ẩn danh",fUser.getEmail(),"",true,"","offline");
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

    private void handleError(Throwable throwable) {
    }

    private void handleResponse(Message message) {
        if (message.getStatus() == 1) {
            Toast.makeText(getApplicationContext(), "Thành công nè", Toast.LENGTH_LONG).show();
            Login(mAuth.getUid());

        }
        if (message.getStatus() == 0)
            Toast.makeText(getApplicationContext(),  "Ủa ??", Toast.LENGTH_LONG).show();
    }
    ///Start Fb
    private void handleFacebookToken(AccessToken token){
        Log.d(TAG, "handleFacebookToken: "+token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Successful ");
                }
                else
                    Log.d(TAG, "onComplete: UnSuccessful ");

            }
        });
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
        DataToken dataToken = new DataToken(StartActivity.this);
        dataToken.saveToken(token.getToken(), token.getRefreshToken());
        startActivity(new Intent(StartActivity.this, MainActivity.class));
        finish();
    }




}