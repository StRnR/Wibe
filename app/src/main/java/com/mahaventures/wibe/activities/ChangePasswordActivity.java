package com.mahaventures.wibe.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.models.ChangePasswordRequestModel;
import com.mahaventures.wibe.services.PostDataService;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mahaventures.wibe.tools.StaticTools.ChangePasswordActivityTag;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MyProfileActivity.class);
        intent.putExtra("from", ChangePasswordActivityTag);
        startActivity(intent);
        finish();
    }

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
        oldPass.setSelection(0);
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

        backBtn.setOnClickListener(v -> onBackPressed());

        showOldPassBtn.setOnClickListener(v -> {
            if (oldPass.getTransformationMethod() != null) {
                oldPass.setTransformationMethod(null);
                showOldPassBtn.setBackground(getDrawable(R.drawable.ic_visibility_blue));
                oldPass.setSelection(oldPass.getText().toString().length());
            } else {
                oldPass.setTransformationMethod(new PasswordTransformationMethod());
                showOldPassBtn.setBackground(getDrawable(R.drawable.ic_visibility));
                oldPass.setSelection(oldPass.getText().toString().length());
            }
        });

        showNewPassBtn.setOnClickListener(v -> {
            if (newPass.getTransformationMethod() != null) {
                newPass.setTransformationMethod(null);
                showNewPassBtn.setBackground(getDrawable(R.drawable.ic_visibility_blue));
                newPass.setSelection(newPass.getText().toString().length());
            } else {
                newPass.setTransformationMethod(new PasswordTransformationMethod());
                showNewPassBtn.setBackground(getDrawable(R.drawable.ic_visibility));
                newPass.setSelection(newPass.getText().toString().length());
            }
        });

        confirmButton.setOnClickListener(v -> {
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            Call call = service.ChangePassword(StaticTools.getToken(), new ChangePasswordRequestModel(oldPass.getText().toString(), newPass.getText().toString()));
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.isSuccessful()) {
                        StaticTools.ShowToast(ChangePasswordActivity.this, "password changed!", 0);
                        startActivity(new Intent(ChangePasswordActivity.this, SignInActivity.class));
                        finish();
                    } else {
                        StaticTools.ShowToast(ChangePasswordActivity.this, "password is invalid", 0);
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    StaticTools.ServerError(ChangePasswordActivity.this, t.getMessage());
                }
            });
        });
    }
}
