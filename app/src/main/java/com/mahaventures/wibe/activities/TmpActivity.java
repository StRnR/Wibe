package com.mahaventures.wibe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;

public class TmpActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);
        Button signOutBtn = findViewById(R.id.btn_signout);

        signOutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TmpActivity.this, MainActivity.class);
            startActivity(intent);
        });


    }
}