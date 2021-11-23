package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noface.fragment.ChangePass;
import com.example.noface.fragment.HomeFragment;
import com.example.noface.fragment.MainChatFragment;
import com.example.noface.fragment.PostByTopicFragment;
import com.example.noface.fragment.PostManagerFragment;
import com.example.noface.fragment.ProfileFragment;
import com.example.noface.fragment.TopicFragment;
import com.example.noface.inter.FragmentInterface;
import com.example.noface.model.User;
import com.example.noface.other.SetAvatar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentInterface {

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_POST_MANAGER = 1;
    private static final int FRAGMENT_TOPIC = 2;
    private static final int FRAGMENT_PASS = 5;
    private static final int FRAGMENT_CHAT = 3;
    private static final int FRAGMENT_PROFILE = 4;
    private static final int MY_REQUEST_CODE = 10;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            if(user == null){
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        //set toolbar thay actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ánh xạ
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);

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


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int idItem = item.getItemId();
        //set fragment cho từng item
        switch (idItem) {
            case R.id.nav_home:
                openHomeFragment();
                break;
            case R.id.nav_post_manager:
                openPostManagerFragment();
                break;
            case R.id.nav_topic:
                openTopicFragment();
                break;
            case R.id.nav_chat:
                openMainChatFragment();
                break;
            case R.id.nav_pass:
                openPassFragment();
                break;
            case R.id.nav_profile:
                openProfileFragment();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
//                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(getApplicationContext(), "Đăng xuất tài khoản", Toast.LENGTH_SHORT).show();
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

        if (user.getDisplayName() == null) {
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


//        Uri photoUrl = user.getPhotoUrl();
//        Glide.with(this).load(photoUrl).error(R.drawable.ic_user).into(imgNavAva);

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
}