package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.BrowseMainAdapter;
import com.mahaventures.wibe.Models.NewModels.Collection;
import com.mahaventures.wibe.Models.NewModels.Page;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.InitModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    private static final int[] navbar_tint_list = {
            R.color.selected_navbar,
            R.color.white,
            R.color.white
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_browse);

        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
//        bottomNavigationView.setItemTextColor(new ColorStateList());

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_browse:
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_mysongs:
                    startActivity(new Intent(getApplicationContext(), MySongsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.main_rv);

        PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
        Call<InitModel> call = service.Init(StaticTools.getToken());
        call.enqueue(new Callback<InitModel>() {
            @Override
            public void onResponse(Call<InitModel> call, Response<InitModel> response) {
                if (response.isSuccessful()){
                    doShit(response.body() != null ? response.body().homePageId : "");
                }
                //todo handle siktir
            }

            @Override
            public void onFailure(Call<InitModel> call, Throwable t) {

            }
        });
    }

    private void doShit(String homePageId) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Page> call = service.GetPage(homePageId);
        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {
                if (response.isSuccessful() && response.body() != null)
                    handleRV(response.body().collections.data);
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {

            }
        });
    }

    private void handleRV(List<Collection> collections) {
        BrowseMainAdapter mainAdapter = new BrowseMainAdapter(collections, BrowseActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mainAdapter);
    }
}
