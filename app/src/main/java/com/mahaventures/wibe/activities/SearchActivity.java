package com.mahaventures.wibe.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.adapters.SearchAlbumAdapter;
import com.mahaventures.wibe.adapters.SearchArtistAdapter;
import com.mahaventures.wibe.adapters.SearchTrackAdapter;
import com.mahaventures.wibe.fragments.MiniPlayerFragment;
import com.mahaventures.wibe.models.Album;
import com.mahaventures.wibe.models.Artist;
import com.mahaventures.wibe.models.GeneralSearch;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.tools.RetrofitClientInstance;
import com.mahaventures.wibe.tools.StaticTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView tracksRecycleView;
    private RecyclerView albumsRecycleView;
    private RecyclerView artistsRecycleView;
    private Timer timer;
    private Button clearTxtBtn;
    private EditText searchText;
    private TextView songsHeader;
    private TextView albumsHeader;
    private TextView artistsHeader;
    private TextView songsShowAll;
    public static List<Track> searchTracks;
    private int pagesCount = 2;
    private int count;
    private boolean isMore;

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_search);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_browse:
                    startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_search:
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticTools.ShowToast(SearchActivity.this, "onCreate", 1);
        setContentView(R.layout.activity_search);
        FrameLayout searchFragmentContainer = findViewById(R.id.fragment_container_search);
        tracksRecycleView = findViewById(R.id.recycler_songs_search);
        albumsRecycleView = findViewById(R.id.recycler_albums_search);
        artistsRecycleView = findViewById(R.id.recycler_artists_search);
        RecyclerView.LayoutManager tracksLayoutManager = new GridLayoutManager(this, 1);
        RecyclerView.LayoutManager albumsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager artistsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tracksRecycleView.setHasFixedSize(true);
        albumsRecycleView.setHasFixedSize(true);
        artistsRecycleView.setHasFixedSize(true);
        tracksRecycleView.setLayoutManager(tracksLayoutManager);
        albumsRecycleView.setLayoutManager(albumsLayoutManager);
        artistsRecycleView.setLayoutManager(artistsLayoutManager);
        clearTxtBtn = findViewById(R.id.btn_clear_search);
        TextView searchHeader = findViewById(R.id.txt_header_search);
        songsHeader = findViewById(R.id.txt_songs_header_search);
        albumsHeader = findViewById(R.id.txt_albums_header_search);
        artistsHeader = findViewById(R.id.txt_artists_header_search);
        songsShowAll = findViewById(R.id.txt_showmore_songs_search);
        songsShowAll.setClickable(true);
        songsShowAll.setOnClickListener(v -> {
            try {
                if (isMore) {
                    showLess();
                    return;
                }
                count = 0;
                searchTracks = new ArrayList<>();
                for (int i = 0; i < pagesCount; i++) {
                    getAllSongs(i + 1);
                }
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (count == pagesCount) {
                            runOnUiThread(() -> {
                                songsShowAll.setClickable(true);
                                songsShowAll.setText("Show Less");
                                SearchTrackAdapter adapter = new SearchTrackAdapter(searchTracks, SearchActivity.this);
                                tracksRecycleView.setAdapter(adapter);
                                isMore = true;
                            });
                            timer.cancel();
                        } else {
                            runOnUiThread(() -> {
                                songsShowAll.setText("Loading...");
                                songsShowAll.setClickable(false);
                            });
                        }
                    }
                }, 0, 50);
            } catch (Exception e) {
                StaticTools.LogErrorMessage("search timer catch");
            }
        });
        tracksRecycleView.setAdapter(null);
        SearchTrackAdapter tmpAdapter = new SearchTrackAdapter(null, SearchActivity.this);
        tracksRecycleView.setAdapter(tmpAdapter);
        searchText = findViewById(R.id.txt_edit_search);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_search);
        bottomNavigationView.setSelectedItemId(R.id.nav_search);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_browse:
                    closeKeyboard();
                    startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_search:
                    closeKeyboard();
                    return true;
                case R.id.nav_mysongs:
                    closeKeyboard();
                    startActivity(new Intent(getApplicationContext(), MySongsActivity.class));
                    overridePendingTransition(0, 0);
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


        FragmentManager fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_search) != null) {
            if (savedInstanceState != null)
                return;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_search, miniPlayerFragment);
            fragmentTransaction.commit();
        }
        MiniPlayerFragment.isPrepared = true;


        searchText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                closeKeyboard();
                search();
                songsHeader.setVisibility(View.VISIBLE);
                albumsHeader.setVisibility(View.VISIBLE);
                artistsHeader.setVisibility(View.VISIBLE);
                songsShowAll.setVisibility(View.VISIBLE);
            }
            return false;
        });

        final View parent = (View) clearTxtBtn.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            clearTxtBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            parent.setTouchDelegate(new TouchDelegate(rect, clearTxtBtn));
        });

        clearTxtBtn.setOnClickListener(v -> searchText.setText(""));
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (searchText.getText().toString().equals(""))
                    clearTxtBtn.setVisibility(View.INVISIBLE);
                else
                    clearTxtBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearTxtBtn.setVisibility(View.VISIBLE);
                if (searchText.getText().toString().equals("")) {
                    clearTxtBtn.setVisibility(View.INVISIBLE);
                } else {
                    clearTxtBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchText.getText().toString().equals(""))
                    clearTxtBtn.setVisibility(View.INVISIBLE);
                else
                    clearTxtBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getAllSongs(int i) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format(Locale.getDefault(), "https://musicify.ir/api/search?query=%s&include=tracks.artists&page=%d", searchText.getText().toString(), i);
        Call<GeneralSearch> call = service.SearchAll(url);
        call.enqueue(new Callback<GeneralSearch>() {
            @Override
            public void onResponse(Call<GeneralSearch> call, Response<GeneralSearch> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            searchTracks.addAll(response.body().tracks.data);
                            count++;
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage() + " wtf is going on");
                        }
                    }
                } else {
                    try {
                        String s1 = new String(response.errorBody().bytes());
                        StaticTools.LogErrorMessage(s1);
                    } catch (Exception e) {
                        StaticTools.LogErrorMessage(e.getMessage() + " wtf");
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralSearch> call, Throwable t) {
                StaticTools.ServerError(SearchActivity.this, t.getMessage());
            }
        });
    }

    private void showLess() {
        count = 0;
        runOnUiThread(() -> {
            songsShowAll.setText("Loading...");
            songsShowAll.setClickable(false);
        });
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format("https://musicify.ir/api/search?query=%s&include=tracks.artists", searchText.getText().toString());
        Call<GeneralSearch> call = service.SearchAll(url);
        call.enqueue(new Callback<GeneralSearch>() {
            @Override
            public void onResponse(Call<GeneralSearch> call, Response<GeneralSearch> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            List<Track> tracks = response.body().tracks.data;
                            tracks = tracks.stream().limit(4).collect(Collectors.toList());
                            SearchTrackAdapter adapter = new SearchTrackAdapter(tracks, SearchActivity.this);
                            searchTracks = new ArrayList<>(response.body().tracks.data);
                            tracksRecycleView.setAdapter(adapter);
                            runOnUiThread(() -> {
                                songsShowAll.setText("Show More");
                                songsShowAll.setClickable(true);
                                isMore = false;
                            });
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage() + " wtf is going on");
                        }
                    }
                } else {
                    try {
                        String s1 = new String(response.errorBody().bytes());
                        StaticTools.LogErrorMessage(s1);
                    } catch (Exception e) {
                        StaticTools.LogErrorMessage(e.getMessage() + " wtf");
                    }
                }
            }

            @Override
            public void onFailure(Call<GeneralSearch> call, Throwable t) {
                StaticTools.ServerError(SearchActivity.this, t.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        StaticTools.ShowToast(SearchActivity.this, "onStart", 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        StaticTools.ShowToast(SearchActivity.this, "onDestroy", 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        StaticTools.ShowToast(SearchActivity.this, "onResume", 1);
        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        StaticTools.ShowToast(SearchActivity.this, "onPause", 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        StaticTools.ShowToast(SearchActivity.this, "onStop", 1);
    }

    //todo call this method on button pressed
    private void search() {
        String txt = searchText.getText().toString();
        if (!txt.equals("")) {
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            String url = String.format("https://musicify.ir/api/search?query=%s&include=tracks.artists", txt);
            Call<GeneralSearch> call = service.SearchAll(url);
            call.enqueue(new Callback<GeneralSearch>() {
                @Override
                public void onResponse(Call<GeneralSearch> call, Response<GeneralSearch> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                List<Album> albums = response.body().albums.data;
//                                albums = albums.stream().limit(3).collect(Collectors.toList());
                                SearchAlbumAdapter albumAdapter = new SearchAlbumAdapter(SearchActivity.this, albums);
                                albumsRecycleView.setAdapter(albumAdapter);
                                List<Artist> artists = response.body().artists.data;
//                                artists = artists.stream().limit(3).collect(Collectors.toList());
                                SearchArtistAdapter artistAdapter = new SearchArtistAdapter(SearchActivity.this, artists);
                                artistsRecycleView.setAdapter(artistAdapter);
                                List<Track> tracks = response.body().tracks.data;
                                tracks = tracks.stream().limit(4).collect(Collectors.toList());
                                SearchTrackAdapter adapter = new SearchTrackAdapter(tracks, SearchActivity.this);
                                searchTracks = new ArrayList<>(response.body().tracks.data);
                                tracksRecycleView.setAdapter(adapter);
                                runOnUiThread(() -> {
                                    songsShowAll.setText("Show More");
                                    songsShowAll.setClickable(true);
                                    isMore = false;
                                });
                            } catch (Exception e) {
                                StaticTools.LogErrorMessage(e.getMessage() + " wtf is going on");
                            }
                        }
                    } else {
                        try {
                            String s1 = new String(response.errorBody().bytes());
                            StaticTools.LogErrorMessage(s1);
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage() + " wtf");
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeneralSearch> call, Throwable t) {
                    StaticTools.ServerError(SearchActivity.this, t.getMessage());
                }
            });

        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void reviveActivity() {
        int a = 2;
    }
}
