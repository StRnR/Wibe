package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.SongsRecyclerPlaylistAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.Playlist;
import com.mahaventures.wibe.Models.NewModels.Tracks;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistActivity extends AppCompatActivity {
    public static FrameLayout playlistFragmentContainer;
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        playlistFragmentContainer = findViewById(R.id.fragment_container_playlist);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_playlist);
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

        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_playlist) != null && MiniPlayerFragment.isLoaded) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_playlist, miniPlayerFragment);
            fragmentTransaction.commit();
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_playlist);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(null);
        SongsRecyclerPlaylistAdapter tmpAdapter = new SongsRecyclerPlaylistAdapter(null, PlaylistActivity.this);
        recyclerView.setAdapter(tmpAdapter);
        Button backBtn = findViewById(R.id.btn_back_playlist);
        Button shuffleBtn = findViewById(R.id.btn_shuffle_playlist);
        ImageView playlistArtwork = findViewById(R.id.img_artwork_playlist);
        TextView playlistTitle = findViewById(R.id.txt_title_playlist);
        TextView playlistOwner = findViewById(R.id.txt_owner_playlist);
        TextView description = findViewById(R.id.txt_playlist_description);

        final View parent = (View) backBtn.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            backBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            parent.setTouchDelegate(new TouchDelegate(rect, backBtn));
        });

        backBtn.setOnClickListener(v -> PlaylistActivity.super.onBackPressed());

        String id = getIntent().getStringExtra("id");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Playlist> playlistCall = service.getPlaylist(StaticTools.getToken(), id);
        playlistCall.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful()) {
                    Picasso.get().load(response.body().image.medium.url).into(playlistArtwork);
                    playlistTitle.setText(response.body().name);
                } else {
                    try {
                        StaticTools.ShowToast(PlaylistActivity.this, response.errorBody().string(), 1);
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                StaticTools.ServerError(PlaylistActivity.this);
            }
        });
        String url = String.format("https://api.musicify.ir/playlists/%s/tracks?include=artists", id);
        Call<Tracks> call = service.getPlaylistTracks(url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful()) {
                    SongsRecyclerPlaylistAdapter adapter = new SongsRecyclerPlaylistAdapter(response.body().data, PlaylistActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {

            }
        });
    }
}
