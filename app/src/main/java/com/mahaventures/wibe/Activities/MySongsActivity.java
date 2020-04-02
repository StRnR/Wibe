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
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.Timer;
import java.util.TimerTask;

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
        RecyclerView recyclerView = findViewById(R.id.recycler_mysongs);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        if (StaticTools.mySong == null || StaticTools.tracks == null) {
            StaticTools.GetMySongs();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (StaticTools.isPrepared) {
                    PlayerActivity.queue = (StaticTools.tracks != null) ? StaticTools.tracks : StaticTools.GetMySongs();
                    MySongsAdapter adapter = new MySongsAdapter(StaticTools.mySong, MySongsActivity.this);
                    runOnUiThread(() -> {
                        recyclerView.setAdapter(adapter);
                    });
                    if (StaticTools.mySong.data.size()==0)
                    {
                        //todo arshia benevis hanooz chizi add nakardi
                    }
                    timer.cancel();
                }
            }
        }, 0, 50);
    }

    private void reviveActivity() {
        int a = 2;
    }

    @Override
    public void onBackPressed() {
    }
}
