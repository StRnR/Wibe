package com.mahaventures.wibe.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.tools.PlayerHandler;

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

//        PlayerComponent component = DaggerPlayerComponent.create();
//        Player player = component.playerBuilder();
//        MediaPlayer mediaPlayer = player.getPlayer();
//        mediaPlayer.stop();
        try {
            PlayerHandler.setUrl("https://cdn.musicify.ir/Track/5e4/736/64a/5e473664adecb7129300df34/file/original/e31fc3846b5c1ddff3695b1b0a121d1667de4227e2a23a931c5985f23c9162a2.mp3");
        } catch (Exception e) {

        }
    }
}
