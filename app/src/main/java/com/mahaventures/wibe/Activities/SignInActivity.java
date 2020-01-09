package com.mahaventures.wibe.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahaventures.wibe.Models.RequestModels.LoginRequestModel;
import com.mahaventures.wibe.Models.RetrofitClientInstance;
import com.mahaventures.wibe.Models.Token;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Services.StaticTools;

import java.lang.reflect.Type;
import java.util.Map;
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
        Button backButton = findViewById(R.id.btn_back_signin);
        Button forgotPassBtn = findViewById(R.id.btn_forgotpass_signin);
        EditText emailTxt = findViewById(R.id.txt_edit_email_sigin);
        EditText passTxt = findViewById(R.id.txt_edit_pass_sigin);
        SpannableString content = new SpannableString("FORGOT PASSWORD");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassBtn.setText(content);
        signInButton.setOnClickListener(view -> {
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            UUID uuid = UUID.randomUUID();
            LoginRequestModel model = new LoginRequestModel(emailTxt.getText().toString(), passTxt.getText().toString(), uuid.toString(), "sd");
            Call<Token> call = service.LoginUser(model);
            call.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    String message = response.message();
                    if (response.isSuccessful()) {
                        Token token = response.body();
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
                public void onFailure(Call<Token> call, Throwable t) {
                    StaticTools.ShowToast(SignInActivity.this, "Sorry sth went wrong pls try again...", 0);
                }
            });
        });

        forgotPassBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPassActivity.class);
            SignInActivity.this.startActivity(intent);
        });

        forgotPassBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            SignInActivity.this.startActivity(intent);
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
