package com.example.noface.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.AvatarAdapter;
import com.example.noface.R;
import com.example.noface.fragment.ProfileFragment;
import com.example.noface.model.Ava;
import com.example.noface.model.Avatar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class DialogFragmentAvatar extends DialogFragment {
    private RecyclerView rclAva;
    private ArrayList<String> lstAva = new ArrayList<>();
    private AvatarAdapter avatarAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_dialog_avatar, container, false);
        rclAva = view.findViewById(R.id.rclAva);

        ///Ava firebase
        getListAva();

        avatarAdapter = new AvatarAdapter(getContext(), lstAva, new AvatarAdapter.AvatarAdapterListener() {
            @Override
            public void click(View v, int position) {

                ((ProfileFragment) getParentFragment()).setAva(lstAva.get(position).trim());
                Toast.makeText(getContext(), "Chọn avatar thành công!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        rclAva.setLayoutManager(gridLayoutManager);

        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("avatars");
        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference file:listResult.getItems()){
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            lstAva.add(uri.toString());

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            rclAva.setAdapter(avatarAdapter);
                        }
                    });
                }
            }
        });
//        LinearLayoutManager linearLayoutManager =
//                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        rclAva.setLayoutManager(linearLayoutManager);
//        rclAva.setAdapter(avatarAdapter);
        return view;
    }

    private void getListAva(){
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("Avatar");
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
//                    Ava ava = dataSnapshot.getValue(Ava.class);
//                    lstAva.add(ava);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Lỗi gòi sư quynh", Toast.LENGTH_SHORT).show();
//            }
//        });


    }


}