package com.mahaventures.wibe.Activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.ResetPassword;
import com.mahaventures.wibe.Models.ResponseModels.ResetPasswordResponseModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);
        Button backBtn = findViewById(R.id.btn_back_resetpass);
        Button resetPassBtn = findViewById(R.id.btn_resetpass);
        EditText codeTxt = findViewById(R.id.txt_code_resetpass);
        EditText newPassTxt = findViewById(R.id.txt_edit_pass_resetpass);

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

        resetPassBtn.setOnClickListener(v -> {
            //todo: verify new password
            GetDataService getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            Call<ResetPasswordResponseModel> call = getDataService.GetResetPasswordId(SignInActivity.Email);
            call.enqueue(new Callback<ResetPasswordResponseModel>() {
                @Override
                public void onResponse(Call<ResetPasswordResponseModel> call, Response<ResetPasswordResponseModel> response) {
                    if (response.isSuccessful()) {
                        ResetPass(response.body().getId(), newPassTxt.getText().toString(), codeTxt.getText().toString());
                    } else {
                        try {
                            StaticTools.ShowToast(ResetPassActivity.this, response.errorBody().string(), 0);
                            Log.wtf("verify error", response.errorBody().string());
                        } catch (Exception e) {
                            Log.wtf("exception", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResetPasswordResponseModel> call, Throwable t) {
                    StaticTools.OnFailure(ResetPassActivity.this);
                }
            });

        });

        backBtn.setOnClickListener(v -> ResetPassActivity.super.onBackPressed());


    }

    private void ResetPass(int id, String newPass, String code) {
        PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
        ResetPassword resetPassword = new ResetPassword(id, code, newPass);
        Call<ResetPassword> call = service.ResetPassword(resetPassword);
        call.enqueue(new Callback<ResetPassword>() {
            @Override
            public void onResponse(Call<ResetPassword> call, Response<ResetPassword> response) {
                if (response.isSuccessful()) {
                    StaticTools.ShowToast(ResetPassActivity.this, "pass reseted", 0);
                } else {
                    try {
                        StaticTools.ShowToast(ResetPassActivity.this, response.errorBody().string(), 0);
                        Log.wtf("treset pass error", response.errorBody().string());
                    } catch (Exception e) {
                        Log.wtf("exception", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResetPassword> call, Throwable t) {
                StaticTools.OnFailure(ResetPassActivity.this);
            }
        });

    }
}
