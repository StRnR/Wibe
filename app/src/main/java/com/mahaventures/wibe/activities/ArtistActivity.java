package com.mahaventures.wibe.activities;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.adapters.ArtistTracksAdapter;
import com.mahaventures.wibe.adapters.SearchAlbumAdapter;
import com.mahaventures.wibe.fragments.MiniPlayerFragment;
import com.mahaventures.wibe.models.Albums;
import com.mahaventures.wibe.models.Artist;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.models.Tracks;
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.tools.AlphaTransformation;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistActivity extends AppCompatActivity {
    private TextView artistName;
    private TextView songsShowMore;
    private ImageView artistArtwork;
    private ImageView artistBlurred;
    private RecyclerView songsRv;
    private RecyclerView albumsRv;
    public static List<Track> tracks;
    private final int pagesCount = 2;
    private int count;
    boolean isMore;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        String id = getIntent().getStringExtra("id");
        artistArtwork = findViewById(R.id.img_artwork_artist_page);
        artistBlurred = findViewById(R.id.img_blur_artist);
        artistName = findViewById(R.id.txt_name_artist);
        Button backBtn = findViewById(R.id.btn_back_artist);
        TextView songsHeader = findViewById(R.id.txt_songs_header_artist);
        TextView albumsHeader = findViewById(R.id.txt_albums_header_search);
        songsShowMore = findViewById(R.id.txt_showmore_songs_artist);
        isMore = false;
        songsShowMore.setClickable(true);
        FrameLayout artistFragmentContainer = findViewById(R.id.fragment_container_artist);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_artist);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
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
                    startActivity(new Intent(getApplicationContext(), MySongsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_artist) != null) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_artist, miniPlayerFragment);
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
        songsShowMore.setOnClickListener(v -> {
            if (isMore) {
                getArtistSongs(id);
                return;
            }
            count = 0;
            tracks = new ArrayList<>();
            for (int i = 0; i < pagesCount; i++) {
                getAllSongs(id, i + 1);
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (count == pagesCount) {
                        runOnUiThread(() -> {
                            songsShowMore.setClickable(true);
                            songsShowMore.setText("Show Less");
                            ArtistTracksAdapter adapter = new ArtistTracksAdapter(tracks, ArtistActivity.this);
                            songsRv.setAdapter(adapter);
                            isMore = true;
                        });
                        timer.cancel();
                    } else {
                        runOnUiThread(() -> {
                            songsShowMore.setText("Loading...");
                            songsShowMore.setClickable(false);
                        });
                    }
                }
            }, 0, 50);
        });
        songsRv = findViewById(R.id.recycler_songs_artist);
        GridLayoutManager tracksLayoutManager = new GridLayoutManager(this, 1);
        songsRv.setLayoutManager(tracksLayoutManager);
        songsRv.setHasFixedSize(true);
        albumsRv = findViewById(R.id.recycler_albums_artist);
        LinearLayoutManager albumsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        albumsRv.setHasFixedSize(true);
        albumsRv.setLayoutManager(albumsLayoutManager);
        getArtistData(id);
        getArtistSongs(id);
        getArtistAlbums(id);
    }

    private void getAllSongs(String id, int i) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format(Locale.getDefault(), "https://api.musicify.ir/artists/%s/tracks?include=artists&page=%d", id, i);
        Call<Tracks> call = service.getArtistTracks(StaticTools.getToken(), url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tracks.addAll(response.body().data);
                    count++;
                }
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {
                StaticTools.ServerError(ArtistActivity.this, t.getMessage());
            }
        });
    }

    private void getArtistAlbums(String id) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format("https://api.musicify.ir/artists/%s/albums", id);
        Call<Albums> call = service.getArtistAlbums(StaticTools.getToken(), url);
        call.enqueue(new Callback<Albums>() {
            @Override
            public void onResponse(Call<Albums> call, Response<Albums> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SearchAlbumAdapter albumAdapter = new SearchAlbumAdapter(ArtistActivity.this, response.body().data, 2);
                    albumsRv.setAdapter(albumAdapter);
                }
            }

            @Override
            public void onFailure(Call<Albums> call, Throwable t) {
                StaticTools.ServerError(ArtistActivity.this, t.getMessage());
            }
        });

    }

    private void getArtistSongs(String id) {
        count = 0;
        runOnUiThread(() -> {
            songsShowMore.setText("Loading...");
            songsShowMore.setClickable(false);
        });
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format("https://api.musicify.ir/artists/%s/tracks?include=artists", id);
        Call<Tracks> call = service.getArtistTracks(StaticTools.getToken(), url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        songsShowMore.setText("Show More");
                        songsShowMore.setClickable(true);
                        isMore = false;
                    });
                    tracks = response.body().data.stream().limit(5).collect(Collectors.toList());
                    ArtistTracksAdapter adapter = new ArtistTracksAdapter(tracks, ArtistActivity.this);
                    songsRv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {
                StaticTools.ServerError(ArtistActivity.this, t.getMessage());
            }
        });
    }

    private void getArtistData(String id) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Artist> call = service.getArtist(StaticTools.getToken(), id);
        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                if (response.isSuccessful()) {
                    Artist artist = response.body() != null ? response.body() : new Artist();
                    artistName.setText(artist.name);
                    RequestCreator requestCreator = Picasso.get().load(artist.image.medium.url);
                    requestCreator.into(artistArtwork, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            ImageView img = new ImageView(ArtistActivity.this);
                            float shadow = 0.5F;
                            requestCreator.resize(artistBlurred.getWidth(), artistBlurred.getHeight()).centerCrop().transform(new BlurTransformation(ArtistActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(img, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    artistBlurred.setBackgroundDrawable(img.getDrawable());
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {
                StaticTools.ServerError(ArtistActivity.this, t.getMessage());
            }
        });
    }
}
