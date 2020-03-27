package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.DBModels.SavedInfo;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.AuthenticationResponseModel;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.SignInRequestModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.orm.SugarContext;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    public static String Email;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        SugarContext.init(SignInActivity.this);
        Button signInButton = findViewById(R.id.btn_signin);
        Button showPassBtn = findViewById(R.id.btn_showpass_signin);
        Button backBtn = findViewById(R.id.btn_back_signin);
        Button forgotPassBtn = findViewById(R.id.btn_forgotpass_signin);
        EditText emailTxt = findViewById(R.id.txt_edit_email_signin);
        EditText passTxt = findViewById(R.id.txt_edit_pass_signin);
        SpannableString content = new SpannableString("FORGOT PASSWORD");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassBtn.setText(content);

        signInButton.setOnClickListener(view -> {
            signInButton.setText(R.string.signing_in_text);
            signInButton.setEnabled(false);
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            String email = emailTxt.getText().toString();
            String pass = passTxt.getText().toString();
            SignInRequestModel model = new SignInRequestModel(email, pass, SignInActivity.this);
            Call<AuthenticationResponseModel> call = service.Authenticate(model);
            call.enqueue(new Callback<AuthenticationResponseModel>() {
                @Override
                public void onResponse(Call<AuthenticationResponseModel> call, Response<AuthenticationResponseModel> response) {
                    signInButton.setText(R.string.sign_in_text);
                    signInButton.setEnabled(true);
                    if (response.isSuccessful()) {
                        try {
                            String token = response.body() != null ? response.body().meta.token : "";
                            SavedInfo.deleteAll(SavedInfo.class);
                            SavedInfo info = new SavedInfo(token, email);
                            info.save();
                            SignInActivity.this.startActivity(new Intent(SignInActivity.this, SearchActivity.class));
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage() + " sign in token error or db saving error");
                        }
                    } else {
                        try {
                            String s = new String(response.errorBody() != null ? response.errorBody().bytes() : new byte[0]);
                            StaticTools.ShowToast(SignInActivity.this, String.format("sign in failed: %s", response.errorBody()), 1);
                        } catch (Exception e) {

                        }
                    }
                }

                @Override
                public void onFailure(Call<AuthenticationResponseModel> call, Throwable t) {
//                    StaticTools.ShowToast(SignInActivity.this, "server error", 0);
//                    signInButton.setText(R.string.sign_in_text);
//                    signInButton.setEnabled(true);
                    StaticTools.ServerError(SignInActivity.this);
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

        showPassBtn.setOnClickListener(v -> {
            if (passTxt.getTransformationMethod() != null) {
                passTxt.setTransformationMethod(null);
                showPassBtn.setBackground(getDrawable(R.drawable.ic_visibility_blue));
                passTxt.setSelection(passTxt.getText().toString().length());
            } else {
                passTxt.setTransformationMethod(new PasswordTransformationMethod());
                showPassBtn.setBackground(getDrawable(R.drawable.ic_visibility));
                passTxt.setSelection(passTxt.getText().toString().length());
            }
        });

        backBtn.setOnClickListener(v -> SignInActivity.super.onBackPressed());

        forgotPassBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPassActivity.class);
            SignInActivity.this.startActivity(intent);
        });
    }

}
