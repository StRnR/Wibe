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

public class ConfirmEmailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmemail);
        Button backBtn = findViewById(R.id.btn_back_confirmemail);
        Button confirmBtn = findViewById(R.id.btn_confirmemail);
        EditText cofirmTxt = findViewById(R.id.txt_edit_confirmemail);

        final View parent = (View) backBtn.getParent();
        parent.post( new Runnable() {
            public void run() {
                final Rect rect = new Rect();
                backBtn.getHitRect(rect);
                rect.top -= 50;
                rect.left -= 50;
                rect.bottom += 50;
                rect.right += 50;
                parent.setTouchDelegate( new TouchDelegate( rect , backBtn));
            }
        });

        backBtn.setOnClickListener(v -> ConfirmEmailActivity.super.onBackPressed());
    }
}