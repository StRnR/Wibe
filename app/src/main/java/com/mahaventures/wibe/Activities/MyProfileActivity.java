package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        SugarContext.init(this);
        Button signOutBtn = findViewById(R.id.btn_signout);
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

        signOutBtn.setOnClickListener(v -> {
            SavedInfo.deleteAll(SavedInfo.class);
            startActivity(new Intent(this, LoadingActivity.class));
        });

    }
}
