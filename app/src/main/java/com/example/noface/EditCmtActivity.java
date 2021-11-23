package com.example.noface;

import static com.example.noface.service.ServiceAPI.BASE_Service;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noface.model.Comment;
import com.example.noface.model.Message;
import com.example.noface.other.DataToken;
import com.example.noface.other.ShowNotifyUser;
import com.example.noface.service.ServiceAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditCmtActivity extends AppCompatActivity {

    private Button btn_huy, btn_save;
    private EditText edtCmt;
    private String cmt, token;
    private int idCmt, idpost;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_edit_cmt);

        DataToken dataToken = new DataToken(getApplicationContext());
        token = dataToken.getToken();

        btn_huy = findViewById(R.id.btn_huy);
        btn_save = findViewById(R.id.btn_save);
        edtCmt = findViewById(R.id.edtCmt);

        Intent intent = getIntent();
        idpost = intent.getIntExtra("idpost", 0);
        idCmt = intent.getIntExtra("id", 0);
        cmt = intent.getStringExtra("cmt");

        edtCmt.setText(cmt);

        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCmt = edtCmt.getText().toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());

                if(newCmt.length() == 0){
                    Toast.makeText(getApplicationContext(), "Hãy nhập bình luận của bạn!", Toast.LENGTH_SHORT).show();
                }else if(newCmt.length() > 100){
                    Toast.makeText(getApplicationContext(), "Bình luận của bạn quá dài!", Toast.LENGTH_SHORT).show();
                }else if(newCmt.equals(cmt)){
                    Toast.makeText(getApplicationContext(), "Không sửa bình luận mà đòi lưu!", Toast.LENGTH_SHORT).show();
                }else {
                    Comment comment = new Comment(idCmt, idpost, user.getUid(), newCmt, strDate, null);
//                  gọi api gửi cmt
                    ShowNotifyUser.showProgressDialog(view.getContext() ,"Đang tải, đừng manh động...");
                    SendCmt(comment);
                }
            }
        });

    }

    private void SendCmt(Comment comment) {
        ServiceAPI requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_Service)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ServiceAPI.class);

        new CompositeDisposable().add(requestInterface.SendCmt(token, comment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCMT, this::handleError)
        );
    }

    private void handleResponseCMT(Message message) {
        if(message.getStatus() == 1){
            Intent intent = new Intent();
            intent.putExtra("result", 1);
            setResult(2, intent);
            finish();
        }
        ShowNotifyUser.dismissProgressDialog();
    }

    private void handleError(Throwable throwable) {
        ShowNotifyUser.dismissProgressDialog();
        ShowNotifyUser.showAlertDialog(this,"Không ổn rồi đại vương ơi! đã có lỗi xảy ra");
    }
}