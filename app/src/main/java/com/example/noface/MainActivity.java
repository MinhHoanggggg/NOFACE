package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.fragment.ChangePass;
import com.example.noface.fragment.FriendFragment;
import com.example.noface.fragment.FriendrequestFragment;
import com.example.noface.fragment.HomeFragment;
import com.example.noface.fragment.MainChatFragment;
import com.example.noface.fragment.NotificationFragment;
import com.example.noface.fragment.PostByTopicFragment;
import com.example.noface.fragment.PostManagerFragment;
import com.example.noface.fragment.ProfileFragment;
import com.example.noface.fragment.TopicFragment;
import com.example.noface.fragment.TrendingFragment;
import com.example.noface.inter.FragmentInterface;
import com.example.noface.model.Achievements;
import com.example.noface.model.Chat;
import com.example.noface.model.Medals;
import com.example.noface.model.Message;
import com.example.noface.model.User;
import com.example.noface.other.DataToken;
import com.example.noface.other.SetAvatar;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentInterface {
    private FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
    private int dem = 0;
    private int MessId=0;
    private String MessCon="";
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_POST_MANAGER = 1;
    private static final int FRAGMENT_TOPIC = 2;
    private static final int FRAGMENT_PASS = 5;
    private static final int FRAGMENT_CHAT = 3;
    private static final int FRAGMENT_PROFILE = 4;
    private static final int FRAGMENT_NOTIFY = 9;
    private static final int MY_REQUEST_CODE = 10;
    private static final int FRAGMENT_TREND = 6;
    private static final int FRAGMENT_FRIEND = 7;
    private static final int FRAGMENT_REQUEST = 8;
    private final ProfileFragment profileFragment = new ProfileFragment();
    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent == null) {
                    return;
                }
                Uri uri = intent.getData();
                if (uri == null) {
                    uri = user.getPhotoUrl();
                }
                profileFragment.setPhotoUri(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    profileFragment.setBitMapImgView(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private int CurrentFragment = FRAGMENT_HOME;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DrawerLayout drawer_layout;
    private NavigationView nav_view;
    private User lUser;
    private String token;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status("online");
        setContentView(R.layout.activity_main);
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);

        if (user == null) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        //set toolbar thay actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get token
        DataToken dataToken = new DataToken(MainActivity.this);
        token = dataToken.getToken();

        //bắt sự kiện click icon home của nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.nav_open, R.string.nav_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        //bắt sự kiện click item của nav
        nav_view.setNavigationItemSelectedListener(this);

        ///Thay doi nav header
        changeHeader(user);

        //mặc định home
        replaceFragment(new HomeFragment());
        nav_view.setCheckedItem(R.id.nav_home);
        setTitleToolbar();

        ///Lay danh hieu
        new Timer().scheduleAtFixedRate(new NewsletterTask(), 0, 10000);

        getMessage();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int idItem = item.getItemId();
        //set fragment cho từng item
        switch (idItem) {
            case R.id.nav_home:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openHomeFragment();
                break;
            case R.id.nav_post_manager:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openPostManagerFragment();
                break;
            case R.id.nav_topic:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openTopicFragment();
                break;
            case R.id.nav_chat:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openMainChatFragment();
                break;
            case R.id.nav_pass:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.salmon));
                openPassFragment();
                break;
            case R.id.nav_profile:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openProfileFragment();
                break;
            case R.id.nav_noti:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openNotificationFragment();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this, StartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(getApplicationContext(), "Đăng xuất tài khoản", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_trend:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openPostTrending();
                break;
            case R.id.nav_fr:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openFriendFragment();
                break;
            case R.id.nav_request:
                toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
                openRequestFragment();
                break;
            default:
                break;
        }
        setTitleToolbar();

        //Đóng drawer
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openRequestFragment() {
        if (CurrentFragment != FRAGMENT_REQUEST) {
            replaceFragment(new FriendrequestFragment());
            CurrentFragment = FRAGMENT_REQUEST;
        }
    }

    private void openFriendFragment() {
        if (CurrentFragment != FRAGMENT_FRIEND) {
            replaceFragment(new FriendFragment());
            CurrentFragment = FRAGMENT_FRIEND;
        }
    }

    private void openPostTrending() {
        if (CurrentFragment != FRAGMENT_TREND) {
            replaceFragment(new TrendingFragment());
            CurrentFragment = FRAGMENT_TREND;
        }
    }

    private void openHomeFragment() {
        if (CurrentFragment != FRAGMENT_HOME) {
            replaceFragment(new HomeFragment());
            CurrentFragment = FRAGMENT_HOME;
        }
    }

    private void openPassFragment() {
        if (CurrentFragment != FRAGMENT_PASS) {
            replaceFragment(new ChangePass());
            CurrentFragment = FRAGMENT_PASS;
        }
    }

    private void openProfileFragment() {
        if (CurrentFragment != FRAGMENT_PROFILE) {
            replaceFragment(profileFragment);
            CurrentFragment = FRAGMENT_PROFILE;
        }
    }

    private void openPostManagerFragment() {
        if (CurrentFragment != FRAGMENT_POST_MANAGER) {
            replaceFragment(new PostManagerFragment());
            CurrentFragment = FRAGMENT_POST_MANAGER;
        }
    }

    private void openTopicFragment() {
        if (CurrentFragment != FRAGMENT_TOPIC) {
            replaceFragment(new TopicFragment());
            CurrentFragment = FRAGMENT_TOPIC;
        }
    }

    private void openMainChatFragment() {
        if (CurrentFragment != FRAGMENT_CHAT) {
            replaceFragment(new MainChatFragment());
            CurrentFragment = FRAGMENT_CHAT;
        }
    }
    private void openNotificationFragment() {
        if (CurrentFragment != FRAGMENT_NOTIFY) {
            replaceFragment(new NotificationFragment());
            CurrentFragment = FRAGMENT_NOTIFY;
        }
    }

    private void setTitleToolbar() {
        String title = "";
        switch (CurrentFragment) {
            case FRAGMENT_HOME: {
                title = "Trang chủ";
                break;
            }
            case FRAGMENT_TOPIC:
                title = "Chủ đề";
                break;
            case FRAGMENT_TREND:
                title = "Nổi bật";
                break;
            case FRAGMENT_FRIEND: {
                title = "Bạn bè";
                break;
            }
            case FRAGMENT_REQUEST: {
                title = "Lời mời kết bạn";
                break;
            }
            case FRAGMENT_CHAT:
                title = "Trò Chuyện";
                break;
            case FRAGMENT_PROFILE: {
                title = "Thông tin tài khoản";
                break;
            }
            case FRAGMENT_POST_MANAGER: {
                title = "Quản lí bài viết";
                break;
            }
            case FRAGMENT_PASS: {
                title = "Đổi mật khẩu";
                break;
            }
            case FRAGMENT_NOTIFY: {
                title = "Thông báo";
                break;
            }
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    public void changeHeader(FirebaseUser user) {
        View headerView = nav_view.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.txtNavName);
        ImageView imgNavAva = headerView.findViewById(R.id.imgNavAva);

        if (user.getDisplayName()== null) {
            navUsername.setText("Ẩn danh");
        } else {
            navUsername.setText(user.getDisplayName());
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String refName = user.getUid().toString();
        DatabaseReference myRef = firebaseDatabase.getReference("Users").child(refName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lUser = snapshot.getValue(User.class);
                if (lUser.getAvaPath() != null) {
                    SetAvatar setAvatar = new SetAvatar();
                    setAvatar.SetAva(imgNavAva, lUser.getAvaPath());
                } else
                    imgNavAva.setImageResource(R.drawable.ic_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    ///xin cap quyen
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getApplicationContext(), "Vui lòng cho phéo truy cập hình ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    @Override
    public void sendData(int id) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PostByTopicFragment fragment = new PostByTopicFragment();

//        Bundle bundle = new Bundle();
//        bundle.putString("id", String.valueOf(id));
//        fragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
        if (fragment != null) {
            fragment.idTopic = id;
        } else {
            Toast.makeText(getApplicationContext(), "Hông có tìm thấy Fragment2 mà đòi truyền", Toast.LENGTH_LONG).show();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    public class NewsletterTask extends TimerTask {
        @Override
        public void run() {
            CheckAchie(user.getUid());
        }
    }

    private void CheckAchie(String id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.CheckAchie(token, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleError(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
    }

    private void handleResponse(Message message) {
        //0 thì thôi, 1 thì là danh hiệu ma mới, 2 là Kẻ nhiều tâm sự, 3 là Ông hoàng thân thiện, 4 là Phù thủy ngôn từ
        if (message.getStatus() != 0) {
            MessId = message.getStatus();
            MessCon = message.getNotification();
            GetMedals(user.getUid());
        }

    }
    public void GetMedals(String id) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetMedals(token,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseMedal, this::handleError)
        );
    }

    private void handleResponseMedal(ArrayList<Achievements> achievements) {
        for(int i=0;i<achievements.size();i++){
            Medals md = achievements.get(i).getMedals();
            if(md.getIDMedal()==MessId){
                ShowNotifyUser.showImgAlertDialog(MainActivity.this,MessCon+" "+md.getMedalName(),md.getImgMedal().trim());
            }
        }
    }

    private void getMessage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dem = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    //last message
                    if (chat.getTo().equals(fuser.getUid()) && chat.isSeen() == false) {
                        dem++;
                    }
                }
                RelativeLayout customLayout = (RelativeLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.notification_badge, null);
                TextView badge = (customLayout.findViewById(R.id.counter));
                if (dem == 0)
                    badge.setVisibility(View.GONE);
                else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(dem+"");
                }
                nav_view.getMenu().findItem(R.id.nav_chat).setActionView(customLayout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}