package com.mahaventures.wibe.Activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.NewModels.TracksResult;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {

    static MediaPlayer mediaPlayer;
    static Track track;
    boolean p = false;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String text = "man";
        String url = String.format("https://api.musicify.ir/tracks/search?query=%s&include=artists,album", text);
        Call<TracksResult> call = service.SearchTracks(url);
        //TODO: in metadata ha bara har song bayad gerefte she az api joz 2 ta avali ke khodam mizanam
        TextView srcTxt = findViewById(R.id.txt_playersrc);
        TextView srcNameTxt = findViewById(R.id.txt_srcname_mainplayer);
        TextView songTitleTxt = findViewById(R.id.txt_title_mainplayer);
        TextView artistTxt = findViewById(R.id.txt_artist_mainplayer);
        TextView songTimeTxt = findViewById(R.id.txt_songtime_mainplayer);
        TextView songDurationTxt = findViewById(R.id.txt_songduration_mainplayer);
        ProgressBar songProgressBar = findViewById(R.id.progressbar_mainplayer);
        ImageView artwork = findViewById(R.id.img_cover_mainplayer);
        ConstraintLayout layout = findViewById(R.id.player_layout);

        call.enqueue(new retrofit2.Callback<TracksResult>() {
            @Override
            public void onResponse(Call<TracksResult> call, Response<TracksResult> response) {
                if (response.isSuccessful()) {
                    try {
                        track = response.body().data.get(0);
                        p = true;
                        songTitleTxt.setText(track.name);
                        String artists = "";
                        if (track.artists.data.size() == 1) {
                            artists = track.artists.data.get(0).name;
                        } else {
                            List<String> strings = track.artists.data.stream().map(x -> x.name).collect(Collectors.toList());
                            artists = TextUtils.join(",", strings);
                        }
                        artistTxt.setText(artists);
                        RequestCreator loaded = Picasso.get().load(track.image.large.url);
                        loaded.into(artwork, new Callback() {
                            @Override
                            public void onSuccess() {
                                layout.setBackgroundDrawable(artwork.getDrawable());
                            }

                            @Override
                            public void onError(Exception e) {
                                StaticTools.LogErrorMessage(e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        StaticTools.LogErrorMessage(e.getMessage());
                    }
                } else {
                    StaticTools.LogErrorMessage(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<TracksResult> call, Throwable t) {

            }
        });


//        while (track==null){
//            int a = 2;
//        }


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (p) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            pos = mediaPlayer.getCurrentPosition();
                            mediaPlayer.stop();
                        } else {
                            try {
                                mediaPlayer.prepare();
                                mediaPlayer.seekTo(pos);
                                mediaPlayer.start();
                            } catch (Exception e) {
                                StaticTools.LogErrorMessage(e.getMessage());
                            }
                        }
                    } else {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(track.file);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage());
                        }
                    }
                }
            }
        }, 0, 10000);
    }
}
