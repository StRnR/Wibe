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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.SearchAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    public static FrameLayout searchFragmentContainer;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Timer timer;

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
        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_search) != null && MiniPlayerFragment.isLoaded) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container_search, miniPlayerFragment);
            fragmentTransaction.commit();
        }
        MiniPlayerFragment.isPrepared = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticTools.LogErrorMessage("search activity started");
        setContentView(R.layout.activity_search);
        searchFragmentContainer = findViewById(R.id.fragment_container_search);
        recyclerView = findViewById(R.id.recycler_search);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        Button clearTxtBtn = findViewById(R.id.btn_clear_search);
        TextView searchHeader = findViewById(R.id.txt_header_search);
        TextView resCategory = findViewById(R.id.txt_result_category_search);
        recyclerView.setAdapter(null);
        SearchAdapter tmpAdapter = new SearchAdapter(null, SearchActivity.this);
        recyclerView.setAdapter(tmpAdapter);
        EditText searchText = findViewById(R.id.txt_edit_search);
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

        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container_search) != null && MiniPlayerFragment.isLoaded) {
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
            }
            return false;
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

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
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
                                                List<Track> tracks = response.body().tracks.data;
                                                SearchAdapter adapter = new SearchAdapter(tracks, SearchActivity.this);
                                                recyclerView.setAdapter(adapter);
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
                                    StaticTools.ShowToast(SearchActivity.this, t.getMessage(), 0);
                                }
                            });

                        }
                    }
                }, 600);


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchText.getText().toString().equals(""))
                    clearTxtBtn.setVisibility(View.INVISIBLE);
                else
                    clearTxtBtn.setVisibility(View.VISIBLE);
            }
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
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
