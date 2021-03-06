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
import com.mahaventures.wibe.models.Album;
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

public class AlbumActivity extends AppCompatActivity {

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        FrameLayout albumFragmentContainer = findViewById(R.id.fragment_container_album);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_album);
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
        RecyclerView recyclerView = findViewById(R.id.recycler_album);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(null);
        Button backBtn = findViewById(R.id.btn_back_album);
        Button shuffleBtn = findViewById(R.id.btn_shuffle_album);
        Button playBtn = findViewById(R.id.btn_play_album);
        ImageView albumArtwork = findViewById(R.id.img_artwork_album);
        ImageView blurredArtwork = findViewById(R.id.img_blur_album);
        TextView albumTitle = findViewById(R.id.txt_title_album);
        TextView albumArtist = findViewById(R.id.txt_owner_album);

        shuffleBtn.setEnabled(false);
        playBtn.setEnabled(false);

        List<Track> tracks = new ArrayList<>();

        shuffleBtn.setOnClickListener(v -> {
            if (tracks.size() > 0) {
                List<Track> tmpTracks = new ArrayList<>(tracks);
                Collections.shuffle(tmpTracks);
                PlayerActivity.from = albumTitle.getText().toString();
                StaticTools.PlayQueue(AlbumActivity.this, tmpTracks);
            }
        });
        playBtn.setOnClickListener(v -> {
            if (tracks.size() > 0) {
                PlayerActivity.from = albumTitle.getText().toString();
                StaticTools.PlayQueue(AlbumActivity.this, tracks);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_album) != null) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_album, miniPlayerFragment);
            fragmentTransaction.commit();
        }

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
        String s = String.format("https://api.musicify.ir/albums/%s?include=artists", id);
        Call<Album> artistCall = service.getAlbum(StaticTools.getToken(), s);
        artistCall.enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                StaticTools.LogErrorMessage(String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    albumTitle.setText(response.body() != null ? response.body().name : "");
                    RequestCreator requestCreator = Picasso.get().load(response.body() != null ? response.body().image.medium.url : "");
                    requestCreator.into(albumArtwork);
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    ImageView img = new ImageView(AlbumActivity.this);
                    float shadow = 0.5F;
                    requestCreator.resize(blurredArtwork.getWidth(), blurredArtwork.getHeight()).centerCrop().transform(new BlurTransformation(AlbumActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            blurredArtwork.setBackgroundDrawable(img.getDrawable());
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {
                StaticTools.ServerError(AlbumActivity.this, t.getMessage());
            }
        });
        String url = String.format("https://api.musicify.ir/albums/%s/tracks?include=artists", id);
        Call<Tracks> call = service.getAlbumTracks(StaticTools.getToken(), url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tracks.addAll(response.body().data);
                    try {
                        albumArtist.setText(response.body().data.get(0).artists.data.get(0).name);
                    } catch (Exception e) {
                        albumArtist.setText("Unknown");
                    }
                    SongsRecyclerAlbumAndPlaylistAdapter adapter = new SongsRecyclerAlbumAndPlaylistAdapter(response.body().data, AlbumActivity.this, albumTitle.getText().toString());
                    recyclerView.setAdapter(adapter);
                    shuffleBtn.setEnabled(true);
                    playBtn.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {
                StaticTools.ServerError(AlbumActivity.this, t.getMessage());
            }
        });

        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
