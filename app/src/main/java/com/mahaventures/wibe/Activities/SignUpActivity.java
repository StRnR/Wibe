package com.mahaventures.wibe.Activities;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahaventures.wibe.Models.RequestModels.SignUpRequestModel;
import com.mahaventures.wibe.Models.RetrofitClientInstance;
import com.mahaventures.wibe.Models.TokenRegister;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.StaticTools;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

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

        backBtn.setOnClickListener(v -> {
            SignUpActivity.super.onBackPressed();
        });

        signUpButton.setOnClickListener(v -> {
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            UUID uuid = UUID.randomUUID();
            SignUpRequestModel model = new SignUpRequestModel(emailTxt.getText().toString(), passwordTxt.getText().toString(), uuid.toString());
            Call<TokenRegister> call = service.SignUpUser(model);
            call.enqueue(new Callback<TokenRegister>() {
                @Override
                public void onResponse(Call<TokenRegister> call, Response<TokenRegister> response) {
                    if (response.isSuccessful()) {
                        TokenRegister token = response.body();
                    } else {
                        try {
                            byte[] bytes = response.errorBody().bytes();
                            LogErrorMessage(bytes);
                        } catch (Exception e) {
                            Log.wtf("exception", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<TokenRegister> call, Throwable t) {

                }
            });
        });
    }

    private void LogErrorMessage(byte[] bytes) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> result = gson.fromJson(new String(bytes), type);
        String str = result.get("detail");
        Log.wtf("server response", str);
    }
}
