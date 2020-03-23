package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Models.NewModels.FavouriteTrack;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySongsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_songs);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_mysongs);

        bottomNavigationView.setSelectedItemId(R.id.nav_mysongs);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_browse:
                    startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_mysongs:
                    return true;
            }
            return false;
        });

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<FavouriteTrack> call = service.GetMySongs(StaticTools.getToken());
        call.enqueue(new Callback<FavouriteTrack>() {
            @Override
            public void onResponse(Call<FavouriteTrack> call, Response<FavouriteTrack> response) {

            }

            @Override
            public void onFailure(Call<FavouriteTrack> call, Throwable t) {

            }
        });
    }
}
