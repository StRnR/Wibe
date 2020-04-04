package com.mahaventures.wibe.Activities;

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
import com.mahaventures.wibe.Adapters.SearchAlbumAdapter;
import com.mahaventures.wibe.Adapters.SearchArtistAdapter;
import com.mahaventures.wibe.Adapters.SearchTrackAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.Album;
import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.Models.NewModels.GeneralSearch;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    public static FragmentManager fragmentManager;
    public static FrameLayout searchFragmentContainer;
    private RecyclerView tracksRecycleView;
    private RecyclerView albumsRecycleView;
    private RecyclerView artistsRecycleView;
    private RecyclerView.LayoutManager tracksLayoutManager;
    private RecyclerView.LayoutManager albumsLayoutManager;
    private RecyclerView.LayoutManager artistsLayoutManager;
    private Timer timer;
    Button clearTxtBtn;
    EditText searchText;
    TextView resCategory;

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
        StaticTools.LogErrorMessage("search activity started");
        setContentView(R.layout.activity_search);
        searchFragmentContainer = findViewById(R.id.fragment_container_search);
        tracksRecycleView = findViewById(R.id.recycler_songs_search);
        albumsRecycleView = findViewById(R.id.recycler_albums_search);
        artistsRecycleView = findViewById(R.id.recycler_artists_search);
        tracksLayoutManager = new GridLayoutManager(this, 1);
        albumsLayoutManager = new GridLayoutManager(this, 1);
        artistsLayoutManager = new GridLayoutManager(this, 1);
        tracksRecycleView.setHasFixedSize(true);
        albumsRecycleView.setHasFixedSize(true);
        artistsRecycleView.setHasFixedSize(true);
        tracksRecycleView.setLayoutManager(tracksLayoutManager);
        albumsRecycleView.setLayoutManager(albumsLayoutManager);
        artistsRecycleView.setLayoutManager(artistsLayoutManager);
        clearTxtBtn = findViewById(R.id.btn_clear_search);
        TextView searchHeader = findViewById(R.id.txt_header_search);
        resCategory = findViewById(R.id.txt_songs_header_search);
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


        fragmentManager = getSupportFragmentManager();
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
                search();
                closeKeyboard();
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

        clearTxtBtn.setOnClickListener(v -> {
            searchText.setText("");
        });
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
                resCategory.setVisibility(View.VISIBLE);
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
                                albums = albums.stream().limit(3).collect(Collectors.toList());
                                SearchAlbumAdapter albumAdapter = new SearchAlbumAdapter(SearchActivity.this,albums);
                                albumsRecycleView.setAdapter(albumAdapter);
                                List<Artist> artists = response.body().artists.data;
                                artists = artists.stream().limit(3).collect(Collectors.toList());
                                SearchArtistAdapter artistAdapter = new SearchArtistAdapter();
                                List<Track> tracks = response.body().tracks.data;
                                tracks = tracks.stream().limit(4).collect(Collectors.toList());
                                SearchTrackAdapter adapter = new SearchTrackAdapter(tracks, SearchActivity.this);
                                tracksRecycleView.setAdapter(adapter);
                                PlayerActivity.queue = new ArrayList<>();
                                PlayerActivity.queue.clear();
                                PlayerActivity.queue.addAll(tracks);
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
