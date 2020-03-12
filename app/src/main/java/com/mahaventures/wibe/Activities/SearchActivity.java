package com.mahaventures.wibe.Activities;

import android.content.Intent;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.Adapters.SongsRecyclerSearchAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Models.NewModels.GeneralSearch;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static FragmentManager fragmentManager;
    private Timer timer;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.recycler_search);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        Button clearTxtBtn = findViewById(R.id.btn_clear_search);
        TextView searchHeader = findViewById(R.id.txt_header_search);
        TextView resCategory = findViewById(R.id.txt_result_category_search);
        recyclerView.setAdapter(null);
        SongsRecyclerSearchAdapter tmpAdapter = new SongsRecyclerSearchAdapter(null, SearchActivity.this);
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

//        ObjectAnimator animation = ObjectAnimator.ofFloat(searchHeader, "translationY", -223f);
//        animation.setDuration(2000);
//        animation.start();


        fragmentManager = getSupportFragmentManager();
        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null)
                return;

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MiniPlayerFragment miniPlayerFragment = new MiniPlayerFragment();
            fragmentTransaction.add(R.id.fragment_container, miniPlayerFragment);
            fragmentTransaction.commit();

        }

//        searchText.setOnClickListener(v -> {
//            final int newTopMargin = 27;
//            Animation a = new Animation() {
//
//                @Override
//                protected void applyTransformation(float interpolatedTime, Transformation t) {
//                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchHeader.getLayoutParams();
//                    params.topMargin = (int) (params.leftMargin + (newTopMargin - params.topMargin) * interpolatedTime);
//                    searchHeader.setLayoutParams(params);
//                }
//            };
//            a.setDuration(2000); // in ms
//            searchHeader.startAnimation(a);
//        });

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
                timer = new Timer();

                if (searchText.getText().toString().equals(""))
                    clearTxtBtn.setVisibility(View.INVISIBLE);
                else
                    clearTxtBtn.setVisibility(View.VISIBLE);

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
}
