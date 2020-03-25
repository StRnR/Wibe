package com.mahaventures.wibe.Activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.NewModels.MyModels.AlbumWithTracks;
import com.mahaventures.wibe.R;

public class AlbumActivity extends AppCompatActivity {
    public static ImageView albumArtwork;
    public static TextView albumTitle;
    public static TextView albumArtist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Button backBtn = findViewById(R.id.btn_back_album);
        Button shuffleBtn = findViewById(R.id.btn_shuffle_album);
        albumArtwork = findViewById(R.id.img_artwork_album);
        albumTitle = findViewById(R.id.txt_title_album);
        albumArtist = findViewById(R.id.txt_owner_album);
        TextView description = findViewById(R.id.txt_album_description);

        final View parent = (View) backBtn.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            backBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            parent.setTouchDelegate(new TouchDelegate(rect, backBtn));
        });
    }
}
