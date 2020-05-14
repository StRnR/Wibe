package com.mahaventures.wibe.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.models.InitModel;
import com.mahaventures.wibe.models.SavedInfo;
import com.mahaventures.wibe.services.PostDataService;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;
import com.orm.SugarContext;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        startActivity(new Intent(this, TmpActivity.class));
        SugarContext.init(this);
        SavedInfo info = SavedInfo.last(SavedInfo.class);
        PlayerActivity.shuffle = false;
        PlayerActivity.repeated = false;
        if (info != null) {
            StaticTools.token = info.getToken();
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            Call<InitModel> call = service.Init(StaticTools.getToken());
            call.enqueue(new Callback<InitModel>() {
                @Override
                public void onResponse(Call<InitModel> call, Response<InitModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().loggedIn != null && response.body().loggedIn) {
                            StaticTools.homePageId = response.body().homePageId;
                            StaticTools.LogErrorMessage("token: " + StaticTools.token);
                            startActivity(new Intent(LoadingActivity.this, BrowseActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                            finish();
                        }
                    } else if (response.code() == 401) {
                        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<InitModel> call, Throwable t) {
                    StaticTools.ServerError(LoadingActivity.this, t.getMessage());
                }
            });
        } else {
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
