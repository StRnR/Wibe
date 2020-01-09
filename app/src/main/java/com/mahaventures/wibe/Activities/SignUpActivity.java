package com.mahaventures.wibe.Activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.StaticTools;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button signUpButton = findViewById(R.id.btn_signup);
        Button backBtn = findViewById(R.id.btn_back_signup);
        TextView passwordTxt = findViewById(R.id.txt_edit_pass_signup);
        ProgressBar bar = findViewById(R.id.passProgressBar);
        TextView weaknessTxt = findViewById(R.id.weaknessTxt);
        weaknessTxt.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        bar.setMax(10);

        passwordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                weaknessTxt.setVisibility(View.VISIBLE);
                bar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pass = s.toString();
                int score = StaticTools.CalculatePasswordStrength(pass);
                if (score == 0) {
                    weaknessTxt.setText("!");
                    int color = getResources().getColor(R.color.unacceptablePass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(2);
                } else if (score <= 4) {
                    weaknessTxt.setText("Weak");
                    int color = getResources().getColor(R.color.weakPass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(4);
                } else if (score <= 6) {
                    weaknessTxt.setText("Good");
                    int color = getResources().getColor(R.color.goodPass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(6);
                } else if (score <= 8) {
                    weaknessTxt.setText("Strong");
                    int color = getResources().getColor(R.color.strongPass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(8);
                } else if (score >= 10) {
                    weaknessTxt.setText("Secure");
                    int color = getResources().getColor(R.color.securePass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(10);
                }
            }
        });
    }
}
