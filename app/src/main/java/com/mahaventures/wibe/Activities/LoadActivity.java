package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.DBModels.SavedInfo;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.InitModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.orm.SugarContext;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadActivity extends AppCompatActivity {
    public static String token;
    public static String homePageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        SugarContext.init(this);
        SavedInfo info = SavedInfo.last(SavedInfo.class);
//        startActivity(new Intent(MainActivity.this, SearchActivity.class));
        if (info != null) {
            token = info.getToken();
            PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
            Call<InitModel> call = service.Init(StaticTools.getToken());
            call.enqueue(new Callback<InitModel>() {
                @Override
                public void onResponse(Call<InitModel> call, Response<InitModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().loggedIn) {
                            homePageId = response.body().homePageId;
                            StaticTools.LogErrorMessage("token: " + token);
                            startActivity(new Intent(LoadActivity.this, SearchActivity.class));
                        } else
                            startActivity(new Intent(LoadActivity.this, MainActivity.class));
                    } else if (response.code() == 401)
                        startActivity(new Intent(LoadActivity.this, MainActivity.class));
                }

                @Override
                public void onFailure(Call<InitModel> call, Throwable t) {

                }
            });
        } else {
            startActivity(new Intent(LoadActivity.this, MainActivity.class));
        }
    }
}
