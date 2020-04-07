package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.EmailVerification;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmEmailActivity extends AppCompatActivity {
    final int duration = 60;
    int sec = duration;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ConfirmEmailActivity.this, SignUpActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String key = getIntent().getStringExtra("key");
        setContentView(R.layout.activity_confirmemail);
        Button backBtn = findViewById(R.id.btn_back_confirmemail);
        Button resendBtn = findViewById(R.id.btn_resend_confirmemail);
        Button confirmBtn = findViewById(R.id.btn_confirmemail);
        EditText confirmTxt = findViewById(R.id.txt_edit_confirmemail);
        SpannableString content = new SpannableString("RESEND CODE");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        resendBtn.setText(content);
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

        backBtn.setOnClickListener(v -> ConfirmEmailActivity.super.onBackPressed());
        resendBtn.setOnClickListener(v -> {
            resendBtn.setEnabled(false);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (check()) {
                        runOnUiThread(() -> {
                            resendBtn.setEnabled(true);
                            SpannableString content = new SpannableString("RESEND CODE");
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            resendBtn.setText(content);
                            sec = duration;
                        });
                        timer.cancel();
                    } else {
                        runOnUiThread(() -> resendBtn.setText(String.valueOf(sec)));
                    }
                }
            }, 0, 1000);
            StaticTools.SendVerificationEmail(ConfirmEmailActivity.this, key, false);
        });
//        confirmBtn.setOnClickListener(v -> {
//            confirmBtn.setEnabled(false);
//            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
//            Call call = service.ConfirmEmail("Bearer " + key, new EmailVerification(confirmTxt.getText().toString()));
//            call.enqueue(new Callback() {
//                @Override
//                public void onResponse(Call call, Response response) {
//                    confirmBtn.setEnabled(true);
//                    if (response.isSuccessful()) {
//                        Intent intent = new Intent(ConfirmEmailActivity.this, TmpActivity.class);
//                        startActivity(intent);
//                    } else {
//                        try {
//                            StaticTools.ShowToast(ConfirmEmailActivity.this, response.errorBody() != null ? response.errorBody().string() : "", 0);
//                            Log.wtf("verify error", response.errorBody().string());
//                        } catch (Exception e) {
//                            Log.wtf("exception", e.getMessage());
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call call, Throwable t) {
//                    confirmBtn.setEnabled(true);
//                    StaticTools.OnFailure(ConfirmEmailActivity.this);
//                }
//            });
//        });
    }

    private boolean check() {
        if (sec <= 0)
            return true;
        else {
            sec--;
            return false;
        }
    }
}
