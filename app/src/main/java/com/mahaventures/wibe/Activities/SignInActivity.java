package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.RequestModels.LoginRequestModel;
import com.mahaventures.wibe.Models.Token;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Button signInButton = findViewById(R.id.btn_signin);
        Button backBtn = findViewById(R.id.btn_back_signin);
        Button forgotPassBtn = findViewById(R.id.btn_forgotpass_signin);
        EditText emailTxt = findViewById(R.id.txt_edit_email_signin);
        EditText passTxt = findViewById(R.id.txt_edit_pass_signin);
        SpannableString content = new SpannableString("FORGOT PASSWORD");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassBtn.setText(content);
        signInButton.setOnClickListener(view -> {
            signInButton.setEnabled(false);
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            UUID uuid = UUID.randomUUID();
            LoginRequestModel model = new LoginRequestModel(emailTxt.getText().toString(), passTxt.getText().toString(), uuid.toString(), "sd");
            Call<Token> call = service.LoginUser(model);
            call.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    signInButton.setEnabled(true);
                    if (response.isSuccessful()) {
                        Token token = response.body();
                        StaticTools.CheckEmailVerification(SignInActivity.this, token.getKey());
                    } else {
                        try {
                            StaticTools.LogErrorMessage(response.errorBody().string());
                            StaticTools.ShowToast(SignInActivity.this, response.errorBody().string(), 0);
                        } catch (Exception e) {
                            Log.wtf("exception", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    signInButton.setEnabled(true);
                    StaticTools.OnFailure(SignInActivity.this);
                }
            });
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

        backBtn.setOnClickListener(v -> SignInActivity.super.onBackPressed());

        forgotPassBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPassActivity.class);
            SignInActivity.this.startActivity(intent);
        });


    }
}
