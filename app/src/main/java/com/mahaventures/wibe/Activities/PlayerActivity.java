package com.mahaventures.wibe.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mahaventures.wibe.R;

import org.w3c.dom.Text;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //TODO: in metadata ha bara har song bayad gerefte she az api joz 2 ta avali ke khodam mizanam
        TextView srcTxt = findViewById(R.id.txt_playersrc);
        TextView srcNameTxt = findViewById(R.id.txt_srcname_mainplayer);
        TextView songTitleTxt = findViewById(R.id.txt_title_mainplayer);
        TextView artistTxt = findViewById(R.id.txt_artist_mainplayer);
        TextView songTimeTxt = findViewById(R.id.txt_songtime_mainplayer);
        TextView songDurationTxt = findViewById(R.id.txt_songduration_mainplayer);
        ProgressBar songProgressBar = findViewById(R.id.progressbar_mainplayer);
        ImageView artwork = findViewById(R.id.img_cover_mainplayer);



    }
}
