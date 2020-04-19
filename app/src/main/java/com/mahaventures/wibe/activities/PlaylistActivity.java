package com.mahaventures.wibe.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.adapters.SongsRecyclerAlbumAndPlaylistAdapter;
import com.mahaventures.wibe.fragments.MiniPlayerFragment;
import com.mahaventures.wibe.models.Playlist;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.models.Tracks;
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.tools.AlphaTransformation;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistActivity extends AppCompatActivity {
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        FrameLayout playlistFragmentContainer = findViewById(R.id.fragment_container_playlist);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
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
        SongsRecyclerAlbumAndPlaylistAdapter tmpAdapter = new SongsRecyclerAlbumAndPlaylistAdapter(null, PlaylistActivity.this);
        recyclerView.setAdapter(tmpAdapter);
        Button backBtn = findViewById(R.id.btn_back_playlist);
        Button shuffleBtn = findViewById(R.id.btn_shuffle_playlist);
        ImageView playlistArtwork = findViewById(R.id.img_artwork_playlist);
        ImageView blurredArtwork = findViewById(R.id.img_blur_playlist);
        TextView playlistTitle = findViewById(R.id.txt_title_playlist);
        TextView playlistOwner = findViewById(R.id.txt_owner_playlist);
        List<Track> tracks = new ArrayList<>();
        shuffleBtn.setEnabled(false);



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

        backBtn.setOnClickListener(v -> onBackPressed());

        String id = getIntent().getStringExtra("id");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Playlist> playlistCall = service.getPlaylist(StaticTools.getToken(), id);
        playlistCall.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful()&& response.body() != null) {
                    RequestCreator requestCreator = Picasso.get().load(response.body().image.medium.url);
                    requestCreator.into(playlistArtwork);
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    ImageView img = new ImageView(PlaylistActivity.this);
                    float shadow = 0.5F;
                    requestCreator.resize(blurredArtwork.getWidth(), blurredArtwork.getHeight()).centerCrop().transform(new BlurTransformation(PlaylistActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            blurredArtwork.setBackgroundDrawable(img.getDrawable());
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                    name = response.body().name;
                    playlistTitle.setText(name);
                } else {
                    try {
                        StaticTools.ShowToast(PlaylistActivity.this, response.errorBody() != null ? response.errorBody().string() : "", 1);
                    } catch (Exception e) {
                        StaticTools.LogErrorMessage("java io Exception");
                    }
                }

            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                StaticTools.ServerError(PlaylistActivity.this, t.getMessage());
            }
        });

        shuffleBtn.setOnClickListener(v -> {
            if (tracks.size()!=0){
                Collections.shuffle(tracks);
                StaticTools.PlayQueue(PlaylistActivity.this, tracks);
            }
        });

        String url = String.format("https://api.musicify.ir/playlists/%s/tracks?include=artists", id);
        Call<Tracks> call = service.getPlaylistTracks(StaticTools.getToken(), url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful()&& response.body() != null) {
                    tracks.addAll(response.body().data);
                    shuffleBtn.setEnabled(true);
                    SongsRecyclerAlbumAndPlaylistAdapter adapter = new SongsRecyclerAlbumAndPlaylistAdapter(response.body().data, PlaylistActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {
                StaticTools.ServerError(PlaylistActivity.this, t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PlaylistActivity.this, BrowseActivity.class));
        finish();
    }
}
