package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signinBtn = findViewById(R.id.btn_signin_main);
        Button signupBtn = findViewById(R.id.btn_signup_main);
        signinBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
            MainActivity.this.startActivity(intent);
        });

        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            MainActivity.this.startActivity(intent);
        });
    }
}
