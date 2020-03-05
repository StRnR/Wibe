package com.mahaventures.wibe.Activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.GeneralSearch;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.SongsRecyclerSearchAdapter;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.recycler_search);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        Button clearTxtBtn = findViewById(R.id.btn_clear_search);
        TextView resCategory = findViewById(R.id.txt_result_category_search);
        recyclerView.setAdapter(null);
        SongsRecyclerSearchAdapter tmpAdapter = new SongsRecyclerSearchAdapter(null, SearchActivity.this);
        recyclerView.setAdapter(tmpAdapter);
        EditText searchText = findViewById(R.id.txt_edit_search);

        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null)
                return;

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container, miniPlayerFragment);
            fragmentTransaction.commit();

        }

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resCategory.setVisibility(View.VISIBLE);
                clearTxtBtn.setVisibility(View.VISIBLE);
                String txt = searchText.getText().toString();
                GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
//                String url = String.format("https://api.musicify.ir/tracks/search?query=%s&include=artists,album", txt);
//                Call<TracksResult> call = service.SearchTracks(url);
//                call.enqueue(new Callback<TracksResult>() {
//                    @Override
//                    public void onResponse(Call<TracksResult> call, Response<TracksResult> response) {
//                        if (!txt.equals("")){
//                            SongsRecyclerSearchAdapter adapter = new SongsRecyclerSearchAdapter(response.body().data,SearchActivity.this);
//                            recyclerView.setAdapter(adapter);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<TracksResult> call, Throwable t) {
//
//                    }
//                });
                if (!txt.equals("")) {
                    String url = String.format("https://musicify.ir/api/search?query=%s&include=tracks.artists", txt);
                    Call<GeneralSearch> call = service.SearchAll(url);
                    call.enqueue(new Callback<GeneralSearch>() {
                        @Override
                        public void onResponse(Call<GeneralSearch> call, Response<GeneralSearch> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    try {
                                        SongsRecyclerSearchAdapter adapter = new SongsRecyclerSearchAdapter(response.body().tracks.data, SearchActivity.this);
                                        recyclerView.setAdapter(adapter);
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

                        }
                    });
                } else {
                    clearTxtBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
}
