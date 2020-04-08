package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    Button confirmBtn;

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
        confirmBtn = findViewById(R.id.btn_confirmemail);
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
            confirmBtn.setEnabled(true);
            confirmBtn.setText("Let’s Go!");
            sendVerification();
        });

        confirmBtn.setOnClickListener(v -> {
            confirmBtn.setEnabled(false);
            confirmBtn.setText("Check your Email...");
            sendVerification();
        });


    }

    private void sendVerification() {
        PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
        Call call = service.VerifyEmail(StaticTools.getToken());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()){
                    StaticTools.ShowToast(ConfirmEmailActivity.this, "Email sent", 1);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                confirmBtn.setEnabled(true);
                confirmBtn.setText("Let’s Go!");
                StaticTools.ServerError(ConfirmEmailActivity.this, t.getMessage());
            }
        });
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
