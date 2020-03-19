package com.mahaventures.wibe.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.DBModels.SavedInfo;
import com.mahaventures.wibe.R;
import com.orm.SugarContext;

public class LoadActivity extends AppCompatActivity {
    public static String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        SugarContext.init(this);
        SavedInfo info = SavedInfo.last(SavedInfo.class);
//        startActivity(new Intent(MainActivity.this, SearchActivity.class));
        if (info != null && info.isActive()) {
            token = info.getToken();
            startActivity(new Intent(LoadActivity.this, SearchActivity.class));
        } else {
            startActivity(new Intent(LoadActivity.this, MainActivity.class));
        }
    }
}
