package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.DBModels.SavedInfo;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.AuthenticationResponseModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.SignUpRequestModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    public static String Email = "";

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
            signInButton.setText(R.string.SigningInText);
            signInButton.setEnabled(false);
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            String email = emailTxt.getText().toString();
            String pass = passTxt.getText().toString();
            Call<AuthenticationResponseModel> call = service.Authenticate(new SignUpRequestModel(email, pass));
            call.enqueue(new Callback<AuthenticationResponseModel>() {
                @Override
                public void onResponse(Call<AuthenticationResponseModel> call, Response<AuthenticationResponseModel> response) {
                    if (response.isSuccessful()) {
                        try {
                            String token = response.body().meta.token;
                            SavedInfo info = new SavedInfo(token, email);
                            info.save();
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage() + " sign in token error");
                        }
                    } else {
                        StaticTools.ShowToast(SignInActivity.this, String.format("sign in failed: %s", response.errorBody()), 1);
                    }
                }

                @Override
                public void onFailure(Call<AuthenticationResponseModel> call, Throwable t) {

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
