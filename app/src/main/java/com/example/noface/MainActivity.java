package com.example.noface;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.noface.fragment.ChangePass;
import com.example.noface.fragment.HomeFragment;
import com.example.noface.fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_PASS = 5;
    private static final int FRAGMENT_PROFILE = 4;
    private static final int MY_REQUEST_CODE = 10;
    private final ProfileFragment profileFragment = new ProfileFragment();
    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== RESULT_OK){
                Intent intent = result.getData();
                if(intent == null){
                    return;
                }
                Uri uri= intent.getData();
                if(uri == null){
                    uri = user.getPhotoUrl();
                }
                profileFragment.setPhotoUri(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




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
            changeHeader();

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
        switch(idItem) {
            case R.id.nav_home:
                openHomeFragment();
                break;

            case R.id.nav_topic:
                startActivity(new Intent(this, TopicActivity.class));
                break;
            case R.id.nav_pass:
                openPassFragment();
                break;
            case R.id.nav_profile:
                openProfileFragment();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                Toast.makeText(getApplicationContext(), "Đăng xuất tài khoản", Toast.LENGTH_SHORT).show();
                break;
            default:break;
        }
        setTitleToolbar();

        //Đóng drawer
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    private void openHomeFragment(){
        if(CurrentFragment != FRAGMENT_HOME){
            replaceFragment(new HomeFragment());
            CurrentFragment = FRAGMENT_HOME;
        }
    }
    private void openPassFragment(){
        if(CurrentFragment != FRAGMENT_PASS){
            replaceFragment(new ChangePass());
            CurrentFragment = FRAGMENT_PASS;
        }
    }

    private void openProfileFragment(){
        if(CurrentFragment != FRAGMENT_PROFILE){
            replaceFragment(profileFragment);
            CurrentFragment = FRAGMENT_PROFILE;
        }
    }

    private void setTitleToolbar(){
        String title = "";
        switch (CurrentFragment){
            case FRAGMENT_HOME:
                title = "Trang chủ";
                break;

//            case ACTIVITY_CATEGORY:
//                title = "Chủ đề";
//                break;
            case FRAGMENT_PROFILE:{
                title = "Thông tin tài khoản";
                break;
            }
        }
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(title);
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
    public void changeHeader(){
        View headerView = nav_view.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.txtNavName);
        if(user.getDisplayName() == null){
            navUsername.setText("Ẩn danh");
        }
        else{
            navUsername.setText(user.getDisplayName());
        }

        ImageView imgNavAva = headerView.findViewById(R.id.imgNavAva);
        Uri photoUrl = user.getPhotoUrl();
        Glide.with(this).load(photoUrl).error(R.drawable.ic_user).into(imgNavAva);

    }
///xin cap quyen
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length>10 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }else{
                Toast.makeText(getApplicationContext(), "Vui lòng cho phéo truy cập hình ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void openGallery(){
     Intent intent = new Intent();
     intent.setType("image/*");
     intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));

    }
}












