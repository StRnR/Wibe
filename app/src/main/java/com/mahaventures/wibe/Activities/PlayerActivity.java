package com.mahaventures.wibe.Activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.NewModels.TracksResult;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.AlphaTransformation;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {

    static MediaPlayer mediaPlayer = new MediaPlayer();
    static Track track;
    int pos = 0;
    SeekBar songProgressBar;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String text = "man";
        String url = String.format("https://api.musicify.ir/tracks/search?query=%s&include=artists,album", text);
        Call<TracksResult> call = service.SearchTracks(url);
        TextView srcTxt = findViewById(R.id.txt_playersrc);
        TextView srcNameTxt = findViewById(R.id.txt_srcname_mainplayer);
        TextView songTitleTxt = findViewById(R.id.txt_title_mainplayer);
        TextView artistTxt = findViewById(R.id.txt_artist_mainplayer);
        TextView songTimeTxt = findViewById(R.id.txt_songtime_mainplayer);
        TextView songDurationTxt = findViewById(R.id.txt_songduration_mainplayer);
        songProgressBar = findViewById(R.id.seekbar_mainplayer);
        ImageView artwork = findViewById(R.id.img_cover_mainplayer);
        Button playBtn = findViewById(R.id.btn_play_mainplayer);
        Button skipBtn = findViewById(R.id.btn_skip_mainplayer);
        Button rewindBtn = findViewById(R.id.btn_rewind_mainplayer);
        ConstraintLayout layout = findViewById(R.id.player_layout);

        songProgressBar.setProgress(0);
        playBtn.setEnabled(false);
        call.enqueue(new retrofit2.Callback<TracksResult>() {
            @Override
            public void onResponse(Call<TracksResult> call, Response<TracksResult> response) {
                if (response.isSuccessful()) {
                    try {
                        track = response.body().data.get(0);
                        ///media player
                        try {
                            playBtn.setBackground(getDrawable(R.drawable.ic_pause));
                            mediaPlayer.setDataSource(track.file);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage());
                        }
                        ///media player
                        int seconds = getDuration(track.file);
                        String str = String.format(Locale.getDefault(), "%d:%d", seconds / 60, seconds % 60);
                        songDurationTxt.setText(str);
                        songTitleTxt.setText(track.name);
                        String artists = "";
                        if (track.artists.data.size() == 1) {
                            artists = track.artists.data.get(0).name;
                        } else {
                            List<String> strings = track.artists.data.stream().map(x -> x.name).collect(Collectors.toList());
                            artists = TextUtils.join(",", strings);
                        }
                        artistTxt.setText(artists);
                        playBtn.setEnabled(true);
                        RequestCreator loaded = Picasso.get().load(track.image.large.url);
                        loaded.into(artwork, new Callback() {
                            @Override
                            public void onSuccess() {
                                //todo uncomment
                                float shadow = 0.5F;
                                loaded.resize(500, 1000).centerCrop().transform(new BlurTransformation(PlayerActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        layout.setBackgroundDrawable(new BitmapDrawable(PlayerActivity.this.getResources(), bitmap));
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        StaticTools.LogErrorMessage(e.getMessage() + " bitmap");
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                StaticTools.LogErrorMessage(e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        StaticTools.LogErrorMessage(e.getMessage() + " player error");
                    }
                } else {
                    StaticTools.LogErrorMessage(response.errorBody().toString() + " api error");
                }
            }

            @Override
            public void onFailure(Call<TracksResult> call, Throwable t) {

            }
        });


        mediaPlayer.setOnPreparedListener(mp -> {
            int duration = mediaPlayer.getDuration();
            int amoungToupdate = duration / 100;
            Timer mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        songProgressBar.setMax(mediaPlayer.getDuration());
                        songProgressBar.setProgress(mediaPlayer.getCurrentPosition());
                    });
                }
            }, 0, amoungToupdate);
        });

        playBtn.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                playBtn.setBackground(getDrawable(R.drawable.ic_play));
                pos = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            } else {
                try {
                    playBtn.setBackground(getDrawable(R.drawable.ic_pause));
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(pos);
                    mediaPlayer.start();
                } catch (Exception e) {
                    StaticTools.LogErrorMessage(e.getMessage());
                }
            }
        });
    }

    int getDuration(String url) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(url, new HashMap<>());
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int timeInmillisec = Integer.parseInt(time);
        int duration = timeInmillisec / 1000;
        int hours = duration / 3600;
        int minutes = (duration - hours * 3600) / 60;
        int seconds = duration - (hours * 3600 + minutes * 60);
        return (minutes * 60) + seconds;
    }

}
