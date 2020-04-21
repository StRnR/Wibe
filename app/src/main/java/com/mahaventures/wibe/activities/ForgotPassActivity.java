package com.mahaventures.wibe.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.services.PostDataService;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        Button backBtn = findViewById(R.id.btn_back_forgotpass);
        Button sendResetLinkBtn = findViewById(R.id.btn_sendresetlink);
        EditText emailTxt = findViewById(R.id.txt_edit_email_forgotpass);
        emailTxt.setSelection(0);
        final View parent = (View) backBtn.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            backBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            parent.setTouchDelegate(new TouchDelegate(rect, backBtn));
        });

        sendResetLinkBtn.setOnClickListener(v -> {
            if (StaticTools.EmailValidation(emailTxt.getText().toString())) {
                StaticTools.ShowToast(ForgotPassActivity.this, "email is not valid", 0);
                return;
            }
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            Call call = service.ResetPassword(StaticTools.getToken(), emailTxt.getText().toString());
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()){
                        StaticTools.ShowToast(ForgotPassActivity.this, "email sent", 0);
                    }else {
                        StaticTools.ShowToast(ForgotPassActivity.this, "this email is not registered", 0);
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        });

        backBtn.setOnClickListener(v -> ForgotPassActivity.super.onBackPressed());

    }
}
