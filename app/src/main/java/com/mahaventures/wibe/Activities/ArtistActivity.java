package com.mahaventures.wibe.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Adapters.ArtistTracksAdapter;
import com.mahaventures.wibe.Adapters.SearchAlbumAdapter;
import com.mahaventures.wibe.Models.NewModels.Albums;
import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.NewModels.Tracks;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.AlphaTransformation;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
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
    TextView artistName;
    TextView songsShowMore;
    ImageView artistArtwork;
    ImageView artistBlurred;
    RecyclerView songsRv;
    RecyclerView albumsRv;
    public static List<Track> tracks;
    private int pagesCount = 2;
    private int count;
    boolean isMore;

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
        songsShowMore.setOnClickListener(v -> {
            if (isMore) {
                showLess(id);
                return;
            }
            count = 0;
            for (int i = 0; i < pagesCount; i++) {
                tracks = new ArrayList<>();
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

    private void showLess(String id) {
        getArtistSongs(id);
    }

    private void getAllSongs(String id, int i) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format(Locale.getDefault(), "https://api.musicify.ir/artists/%s/tracks?include=artists&page=%d", id, i);
        Call<Tracks> call = service.getArtistTracks(StaticTools.getToken(), url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful()) {
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
                if (response.isSuccessful()) {
                    SearchAlbumAdapter albumAdapter = new SearchAlbumAdapter(ArtistActivity.this, response.body().data);
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
                if (response.isSuccessful()) {
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
