package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.DBModels.SavedInfo;
import com.mahaventures.wibe.R;
import com.orm.SugarContext;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        SugarContext.init(this);
        Button signOutBtn = findViewById(R.id.btn_signout);
        signOutBtn.setOnClickListener(v -> {
            SavedInfo.deleteAll(SavedInfo.class);
            startActivity(new Intent(this, LoadActivity.class));
        });
    }
}
