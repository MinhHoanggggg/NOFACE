package com.example.noface.fragment;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noface.Adapter.TopicAdapter;
import com.example.noface.MainActivity;
import com.example.noface.R;
import com.example.noface.inter.FragmentInterface;
import com.example.noface.model.Topic;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TopicFragment extends Fragment {

    private RecyclerView rcvTopicList;
    private FragmentInterface iClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) this.iClickListener = (FragmentInterface) context;
        else
            throw new RuntimeException(context.toString() + "must implement onViewSelected!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_topic, container, false);

        rcvTopicList = view.findViewById(R.id.rcvTopicList);

        //init rcv
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rcvTopicList.setLayoutManager(gridLayoutManager);

        //cuộn nuột hơn
        rcvTopicList.setHasFixedSize(true);

        ShowNotifyUser.showProgressDialog(getContext(),"Đang tải, đừng mang động...");
        //get data từ api
        GetAllTopic();
        return view;
    }

    //    get data từ API
    private void GetAllTopic() {

        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.GetAllTopic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ArrayList<Topic> topics) {
        try {

            //set adapter cho rcv
            TopicAdapter topicAdapter = new TopicAdapter(topics, getContext(), new TopicAdapter.TopicAdapterListener() {
                @Override
                public void click(View view, int position) {
                  int topicId =  topics.get(position).getIDTopic();
                    iClickListener.sendData(topicId);
                }
            });
            rcvTopicList.setAdapter(topicAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
        ShowNotifyUser.dismissProgressDialog();
    }


    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(getContext(),"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }
}
