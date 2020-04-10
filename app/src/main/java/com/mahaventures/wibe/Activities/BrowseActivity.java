package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.BrowseMainAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.Collection;
import com.mahaventures.wibe.Models.NewModels.Page;
import com.mahaventures.wibe.Models.NewModels.ProfileModels.InitModel;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Services.PostDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mahaventures.wibe.Tools.StaticTools.getToken;

public class BrowseActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;
    public FrameLayout browseFragmentContainer;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_browse);
        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
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
        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Button myProfileBtn = findViewById(R.id.btn_user_browse);
        refreshLayout = findViewById(R.id.browse_sr);
        refreshLayout.setOnRefreshListener(this::getHPI);
        browseFragmentContainer = findViewById(R.id.fragment_container_browse);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_browse);
        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
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

        myProfileBtn.setOnClickListener(v -> {
            BrowseActivity.this.startActivity(new Intent(BrowseActivity.this, MyProfileActivity.class));
        });

        Timer timerRevive = new Timer();
        timerRevive.schedule(new TimerTask() {
            @Override
            public void run() {
                reviveActivity();
            }
        }, 1000, 1000);

        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_browse) != null) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_browse, miniPlayerFragment);
            fragmentTransaction.commit();
        }
        MiniPlayerFragment.isPrepared = true;
        recyclerView = findViewById(R.id.main_recyclerview_browse);
        if (StaticTools.homePageId != null && !StaticTools.homePageId.equals("")) {
            doShit(StaticTools.homePageId);
        } else {
            getHPI();
        }
    }

    private void getHPI() {
        PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
        Call<InitModel> call = service.Init(getToken());
        call.enqueue(new Callback<InitModel>() {
            @Override
            public void onResponse(Call<InitModel> call, Response<InitModel> response) {
                if (response.isSuccessful()) {
                    String s = response.body() != null ? response.body().homePageId : "";
                    doShit(s);
                }
            }

            @Override
            public void onFailure(Call<InitModel> call, Throwable t) {
                StaticTools.ServerError(BrowseActivity.this, t.getMessage());
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
                StaticTools.ServerError(BrowseActivity.this, t.getMessage());
            }
        });
    }

    private void handleRV(List<Collection> collections) {
        BrowseMainAdapter mainAdapter = new BrowseMainAdapter(collections, BrowseActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mainAdapter);
        refreshLayout.setRefreshing(false);
    }

    private void reviveActivity() {
        int a = 2;
    }
}
