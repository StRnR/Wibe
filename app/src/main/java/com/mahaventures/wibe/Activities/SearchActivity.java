package com.mahaventures.wibe.Activities;

import android.os.Bundle;
import android.os.StatFs;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mahaventures.wibe.Models.NewModels.GeneralSearch;
import com.mahaventures.wibe.Models.NewModels.TracksResult;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.recycler_search);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(null);
        EditText searchText = findViewById(R.id.txt_edit_search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                    Call<GeneralSearch> call = service.SearchAll(txt);
                    call.enqueue(new Callback<GeneralSearch>() {
                        @Override
                        public void onResponse(Call<GeneralSearch> call, Response<GeneralSearch> response) {
                            if (response.isSuccessful()) {
                                SongsRecyclerSearchAdapter adapter = new SongsRecyclerSearchAdapter(response.body() != null ? response.body().tracks : null, SearchActivity.this);
                                recyclerView.setAdapter(adapter);
                            } else {
                                try {
                                    String s1 = new String(response.errorBody().bytes());
                                    StaticTools.LogErrorMessage(s1);
                                } catch (Exception e) {

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GeneralSearch> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
