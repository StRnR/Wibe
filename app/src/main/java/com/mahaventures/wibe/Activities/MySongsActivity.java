package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.MySongsAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.MySong;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySongsActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;
    public static FrameLayout mysongsFragmentContainer;

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


        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_mysongs) != null ) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_mysongs, miniPlayerFragment);
            fragmentTransaction.commit();
        }
        MiniPlayerFragment.isPrepared = true;
        RecyclerView recyclerView = findViewById(R.id.recycler_mysongs);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = "https://api.musicify.ir/profile/tracks?include=track.album,track.artists";
        Call<MySong> call = service.GetMySongs(StaticTools.getToken(), url);
        call.enqueue(new Callback<MySong>() {
            @Override
            public void onResponse(Call<MySong> call, Response<MySong> response) {
                if (response.isSuccessful()) {
                    //todo queue
                    PlayerActivity.queue = response.body().data.stream().map(x->x.track).collect(Collectors.toList());
                    MySongsAdapter adapter = new MySongsAdapter(response.body(), MySongsActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<MySong> call, Throwable t) {

            }
        });
    }
}
