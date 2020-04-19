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
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.tools.RetrofitClientInstance;

public class ResetPassActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        Button backBtn = findViewById(R.id.btn_back_resetpass);
        Button resetPassBtn = findViewById(R.id.btn_resetpass);
        EditText newPassTxt = findViewById(R.id.txt_edit_pass_resetpass);
        newPassTxt.setSelection(0);
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

        resetPassBtn.setOnClickListener(v -> {
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        });
        backBtn.setOnClickListener(v -> ResetPassActivity.super.onBackPressed());
    }
}
