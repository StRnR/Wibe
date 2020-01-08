package com.mahaventures.wibe.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.RequestModels.LoginRequestModel;
import com.mahaventures.wibe.Models.RetrofitClientInstance;
import com.mahaventures.wibe.Models.Token;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
                LoginRequestModel model = new LoginRequestModel("testmail@gmail.com", "password", "ss", "sd");
                Call<Token> call = service.LoginUser(model);
                call.enqueue(new Callback<Token>() {

                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {

                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {

                    }
                });
            }
        });

    }
}
