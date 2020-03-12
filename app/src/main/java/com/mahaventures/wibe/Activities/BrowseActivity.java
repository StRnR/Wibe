package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mahaventures.wibe.R;

public class BrowseActivity extends AppCompatActivity {

    private static final int[] navbar_tint_list = {
            R.color.selected_navbar,
            R.color.white,
            R.color.white
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar_bottom_browse);

        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
//        bottomNavigationView.setItemTextColor(new ColorStateList());

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
    }
}
