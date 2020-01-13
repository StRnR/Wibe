package com.mahaventures.wibe.Activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;

public class ForgotPassActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        Button backBtn = findViewById(R.id.btn_back_forgotpass);
        Button sendResetLinkBtn = findViewById(R.id.btn_sendresetlink);
        EditText emailTxt = findViewById(R.id.txt_edit_email_forgotpass);

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

        backBtn.setOnClickListener(v -> ForgotPassActivity.super.onBackPressed());

    }
}
