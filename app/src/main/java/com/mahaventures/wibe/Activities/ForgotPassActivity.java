package com.mahaventures.wibe.Activities;

import android.os.Bundle;
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
        Button resetPassBtn = findViewById(R.id.btn_sendresetlink);
        EditText emailTxt = findViewById(R.id.txt_edit_email_forgotpass);

    }
}
