package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.MySongsAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.MySong;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySongsActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;
    public static FrameLayout mysongsFragmentContainer;
    public static List<Track> mySongTracks;
    public static MySong mySong;
    TextView emptyTxt;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_songs);
        refreshLayout = findViewById(R.id.browse_sr);
        refreshLayout.setOnRefreshListener(this::GetMySongs);
        emptyTxt = findViewById(R.id.txt_empty_mysongs);
        mysongsFragmentContainer = findViewById(R.id.fragment_container_mysongs);
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

        Timer timerRevive = new Timer();
        timerRevive.schedule(new TimerTask() {
            @Override
            public void run() {
                reviveActivity();
            }
        }, 1000, 1000);


        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_mysongs) != null) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_mysongs, miniPlayerFragment);
            fragmentTransaction.commit();
        }
        MiniPlayerFragment.isPrepared = true;
        recyclerView = findViewById(R.id.recycler_mysongs);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        GetMySongs();
    }

    private void GetMySongs() {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = "https://api.musicify.ir/profile/tracks?include=track.album,track.artists";
        Call<MySong> call = service.GetMySongs(StaticTools.getToken(), url);
        call.enqueue(new Callback<MySong>() {
            @Override
            public void onResponse(Call<MySong> call, Response<MySong> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mySongTracks = response.body().data.stream().map(x -> x.track).collect(Collectors.toList());
                    if (mySongTracks.size() == 0) {
                        emptyTxt.setVisibility(View.VISIBLE);
                    } else {
                        emptyTxt.setVisibility(View.GONE);
                    }
                    mySong = response.body();
                    MySongsAdapter adapter = new MySongsAdapter(StaticTools.mySong, MySongsActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<MySong> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiniPlayerFragment.isPrepared = true;
    }

    private void reviveActivity() {
        int a = 2;
    }

    @Override
    public void onBackPressed() {
    }
}
