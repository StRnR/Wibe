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

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.DBModels.SavedInfo;
import com.mahaventures.wibe.R;
import com.orm.SugarContext;

public class MyProfileActivity extends AppCompatActivity {
    EditText name;
    EditText email;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, BrowseActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        SugarContext.init(this);
        Button signOutBtn = findViewById(R.id.btn_signout);
        Button backBtn = findViewById(R.id.btn_back_my_profile);
        Button changePassBtn = findViewById(R.id.btn_change_pass_my_profile);
        name = findViewById(R.id.txt_edit_name_my_profile);
        name.setFocusable(false);
        name.setEnabled(false);
        name.setCursorVisible(false);
        name.setKeyListener(null);
        name.setText(SavedInfo.last(SavedInfo.class).getName());

        email = findViewById(R.id.txt_edit_email_my_profile);
        email.setFocusable(false);
        email.setEnabled(false);
        email.setCursorVisible(false);
        email.setKeyListener(null);
        email.setText(SavedInfo.last(SavedInfo.class).getEmail());

        SpannableString content = new SpannableString("CHANGE PASSWORD");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        changePassBtn.setText(content);
        changePassBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });
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
        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
        signOutBtn.setOnClickListener(v -> {
            SavedInfo.deleteAll(SavedInfo.class);
            startActivity(new Intent(this, LoadingActivity.class));
        });

    }
}
