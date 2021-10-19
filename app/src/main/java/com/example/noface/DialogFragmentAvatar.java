package com.example.noface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.AvatarAdapter;
import com.example.noface.fragment.ProfileFragment;
import com.example.noface.model.Avatar;

import java.util.ArrayList;


public class DialogFragmentAvatar extends DialogFragment {
    private RecyclerView rclAva;
    private ArrayList<Avatar> lstAva = new ArrayList<>();
    private AvatarAdapter avatarAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_dialog_avatar, container, false);
        rclAva = view.findViewById(R.id.rclAva);




        for(int i=1;i<11;i++){

            Avatar avatar = new Avatar(i,"ava"+i);
            lstAva.add(avatar);
        }
        avatarAdapter = new AvatarAdapter(getContext(), lstAva, new AvatarAdapter.AvatarAdapterListener() {
            @Override
            public void click(View v, int position) {

                ((ProfileFragment) getParentFragment()).setAva(lstAva.get(position).getAvaPath());
                Toast.makeText(getContext(), "Chọn avatar thành công!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false);
        rclAva.setLayoutManager(gridLayoutManager);
        rclAva.setAdapter(avatarAdapter);
//        LinearLayoutManager linearLayoutManager =
//                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        rclAva.setLayoutManager(linearLayoutManager);
//        rclAva.setAdapter(avatarAdapter);
        return view;
    }


}