package com.mahaventures.wibe.activities;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.di.DaggerPlayerComponent;
import com.mahaventures.wibe.di.Player;
import com.mahaventures.wibe.di.PlayerComponent;

public class TmpActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp);
//        Button signOutBtn = findViewById(R.id.btn_signout);
//
//        signOutBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(TmpActivity.this, MainActivity.class);
//            startActivity(intent);
//        });

        PlayerComponent component = DaggerPlayerComponent.create();
        Player player = component.playerBuilder();
        MediaPlayer mediaPlayer = player.getPlayer();
        mediaPlayer.stop();


    }
}
