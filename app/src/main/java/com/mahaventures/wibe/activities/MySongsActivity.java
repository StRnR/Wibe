package com.mahaventures.wibe.activities;

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
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.adapters.MySongsAdapter;
import com.mahaventures.wibe.fragments.MiniPlayerFragment;
import com.mahaventures.wibe.models.MySong;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.tools.PlayerHandler;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySongsActivity extends AppCompatActivity {
    public static List<Track> mySongTracks = new ArrayList<>();
    private TextView emptyTxt;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private int total = 1;
    private int current = 0;
    private int x;
    private int y;

    @Override
    public void onBackPressed() {
        try {
            PlayerHandler.stop();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_songs);
        refreshLayout = findViewById(R.id.mySong_sr);
        refreshLayout.setOnRefreshListener(() -> {
            mySongTracks.clear();
            current = 0;
            GetMySongs();
        });
        refreshLayout.setEnabled(false);
        emptyTxt = findViewById(R.id.txt_empty_mysongs);
        FrameLayout mySongsFragmentContainer = findViewById(R.id.fragment_container_mysongs);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_mysongs);
        bottomNavigationView.setSelectedItemId(R.id.nav_mysongs);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_browse:
                    Intent intent1 = new Intent(getApplicationContext(), BrowseActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent1);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_search:
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_mysongs:
                    return true;
            }
            return false;
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
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
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            x = scrollX;
            y = scrollY;
            if (!recyclerView.canScrollVertically(1)) {
                if (current <= total) {
                    GetMySongs();
                }
            }
        });
        GetMySongs();
    }

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

    private void GetMySongs() {
        current++;
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format(Locale.getDefault(), "https://api.musicify.ir/profile/tracks?include=track.album,track.artists&page=%d", current);
        Call<MySong> call = service.GetMySongs(StaticTools.getToken(), url);
        call.enqueue(new Callback<MySong>() {
            @Override
            public void onResponse(Call<MySong> call, Response<MySong> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mySongTracks.addAll(response.body().data.stream().map(x -> x.track).collect(Collectors.toList()));
                    mySongTracks = mySongTracks.stream().distinct().collect(Collectors.toList());
                    if (mySongTracks.size() == 0) {
                        emptyTxt.setVisibility(View.VISIBLE);
                    } else {
                        emptyTxt.setVisibility(View.GONE);
                    }
                    MySongsAdapter adapter = new MySongsAdapter(mySongTracks, MySongsActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.scrollTo(x, y);
                    refreshLayout.setRefreshing(false);
                    refreshLayout.setEnabled(true);
                    total = response.body().meta.pagination.totalPages;
                }
            }

            @Override
            public void onFailure(Call<MySong> call, Throwable t) {
                StaticTools.ServerError(MySongsActivity.this, t.getMessage());
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MiniPlayerFragment.isPrepared = true;
    }
}
