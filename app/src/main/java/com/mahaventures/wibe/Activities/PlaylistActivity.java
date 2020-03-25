package com.mahaventures.wibe.Activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mahaventures.wibe.Models.NewModels.MyModels.PlaylistWithTracks;
import com.mahaventures.wibe.Models.NewModels.Playlist;
import com.mahaventures.wibe.Models.NewModels.Tracks;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Button backBtn = findViewById(R.id.btn_back_playlist);
        Button shuffleBtn = findViewById(R.id.btn_shuffle_playlist);
        ImageView playlistArtwork = findViewById(R.id.img_artwork_playlist);
        TextView playlistTitle = findViewById(R.id.txt_title_playlist);
        TextView playlistOwner = findViewById(R.id.txt_owner_playlist);
        TextView description = findViewById(R.id.txt_playlist_description);


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

        String id = getIntent().getStringExtra("id");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Playlist> playlistCall = service.getPlaylist(id);
        playlistCall.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                if (response.isSuccessful()){
                    Picasso.get().load(response.body().image.medium.url).into(playlistArtwork);
                    playlistTitle.setText(response.body().name);
                }

            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {

            }
        });
        String url = String.format("https://api.musicify.ir/playlist/%s/tracks?include=artists", id);
        Call<Tracks> call = service.getPlaylistTracks(url);
        call.enqueue(new Callback<Tracks>() {
            @Override
            public void onResponse(Call<Tracks> call, Response<Tracks> response) {
                if (response.isSuccessful()) {
                    //do track shit
                }
            }

            @Override
            public void onFailure(Call<Tracks> call, Throwable t) {

            }
        });
    }
}
