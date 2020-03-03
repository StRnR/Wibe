package com.mahaventures.wibe.Activities;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.NewModels.ProfileModels.RegisterResponseModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.SignUpRequestModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button signUpButton = findViewById(R.id.btn_signup);
        Button backBtn = findViewById(R.id.btn_back_signup);
        EditText nameTxt = findViewById(R.id.txt_edit_name_signup);
        EditText emailTxt = findViewById(R.id.txt_edit_email_signup);
        EditText passwordTxt = findViewById(R.id.txt_edit_pass_signup);
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
                    weaknessTxt.setText("try harder!");
                    int color = getResources().getColor(R.color.unacceptablePass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(2);
                } else if (score <= 4) {
                    weaknessTxt.setText("this sucks!");
                    int color = getResources().getColor(R.color.weakPass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(4);
                } else if (score <= 6) {
                    weaknessTxt.setText("ok i guess");
                    int color = getResources().getColor(R.color.goodPass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(6);
                } else if (score <= 8) {
                    weaknessTxt.setText("good one!");
                    int color = getResources().getColor(R.color.strongPass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(8);
                } else if (score >= 10) {
                    weaknessTxt.setText("mr.robot");
                    int color = getResources().getColor(R.color.securePass);
                    bar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    bar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    weaknessTxt.setTextColor(color);
                    bar.setProgress(10);
                }
            }
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
            SignUpActivity.super.onBackPressed();
        });

        signUpButton.setOnClickListener(v -> {
            signUpButton.setEnabled(false);
            signUpButton.setText(R.string.SigningUpText);
            String email = emailTxt.getText().toString();
            String pass = passwordTxt.getText().toString();
            if (!StaticTools.EmailValidation(email)) {
                StaticTools.ShowToast(SignUpActivity.this, "Email is not valid.", 0);
                return;
            }
            if (StaticTools.CalculatePasswordStrength(pass) == 0) {
                StaticTools.ShowToast(SignUpActivity.this, "Password must be at least 6 characters", 0);
                return;
            }
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            Call<RegisterResponseModel> call = service.Register(new SignUpRequestModel(email, pass));
            call.enqueue(new Callback<RegisterResponseModel>() {
                @Override
                public void onResponse(Call<RegisterResponseModel> call, Response<RegisterResponseModel> response) {
                    signUpButton.setEnabled(true);
                    signUpButton.setText(R.string.SignUpText);
                    if (response.isSuccessful()) {
                        StaticTools.ShowToast(SignUpActivity.this, "User registered succesfully", 1);
                        String token = response.body().data.id;
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String s = new String(response.errorBody().bytes());
                                StaticTools.LogErrorMessage(s);
                            }
                        } catch (Exception e) {

                        }
                        StaticTools.LogErrorMessage(response.errorBody().toString() + " signup error");
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponseModel> call, Throwable t) {
                    signUpButton.setEnabled(true);
                    signUpButton.setText(R.string.SignUpText);
                    StaticTools.LogErrorMessage(t.getMessage() + " server error on signup");
                }
            });
        });
    }


}
