package com.mahaventures.wibe.activities;

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
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.adapters.BrowseMainAdapter;
import com.mahaventures.wibe.fragments.MiniPlayerFragment;
import com.mahaventures.wibe.models.Collection;
import com.mahaventures.wibe.models.InitModel;
import com.mahaventures.wibe.models.Page;
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.services.PostDataService;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mahaventures.wibe.tools.StaticTools.BrowseActivityTag;
import static com.mahaventures.wibe.tools.StaticTools.getToken;

public class BrowseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

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
//        startActivity(new Intent(this, TmpActivity.class));
        Button myProfileBtn = findViewById(R.id.btn_user_browse);
        refreshLayout = findViewById(R.id.browse_sr);
        refreshLayout.setOnRefreshListener(this::getHPI);
        refreshLayout.setEnabled(false);
        FrameLayout browseFragmentContainer = findViewById(R.id.fragment_container_browse);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_browse);
        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_browse:
                    return true;
                case R.id.nav_search:
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_mysongs:
                    Intent intent1 = new Intent(getApplicationContext(), MySongsActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent1);
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });

        myProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(BrowseActivity.this, MyProfileActivity.class);
            intent.putExtra("from", BrowseActivityTag);
            startActivity(intent);
        });

        Timer timerRevive = new Timer();
        timerRevive.schedule(new TimerTask() {
            @Override
            public void run() {
                reviveActivity();
            }
        }, 1000, 1000);

        FragmentManager fragmentManager = getSupportFragmentManager();
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
        refreshLayout.setEnabled(false);
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
        refreshLayout.setEnabled(false);
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
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);
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
        refreshLayout.setEnabled(true);
    }

    private void reviveActivity() {
        int a = 2;
    }
}
