package com.mahaventures.wibe.Activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.NewModels.ProfileModels.ChangePasswordRequestModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        EditText newPass = findViewById(R.id.txt_edit_new_pass_change_pass);
        EditText oldPass = findViewById(R.id.txt_edit_old_pass_change_pass);
        Button confirmButton = findViewById(R.id.btn_confirm_change_pass);
        Button showNewPassBtn = findViewById(R.id.btn_show_new_pass_change_pass);
        Button showOldPassBtn = findViewById(R.id.btn_show_old_pass_change_pass);
        Button backBtn = findViewById(R.id.btn_back_change_password);
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
            onBackPressed();
        });

        confirmButton.setOnClickListener(v -> {
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            Call call = service.ChangePassword(StaticTools.getToken(), new ChangePasswordRequestModel(oldPass.getText().toString(), newPass.getText().toString()));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        StaticTools.ShowToast(ChangePasswordActivity.this, "password changed!", 0);
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        });
    }
}
