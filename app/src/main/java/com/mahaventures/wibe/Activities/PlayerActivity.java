package com.mahaventures.wibe.Activities;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Tools.AlphaTransformation;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
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
    SeekBar songSeekBar;
    boolean isPlaying;

//    @Override
//    public void onBackPressed() {
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        String trackId = getIntent().getStringExtra("id");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format("https://api.musicify.ir/tracks/%s?include=artists,album", trackId);
        mediaPlayer.stop();
        mediaPlayer.reset();
        Call<Track> call = service.GetTrackById(url);
        //TODO: in metadata ha bara har song bayad gerefte she az api joz 2 ta avali ke khodam mizanam
        TextView srcTxt = findViewById(R.id.txt_playersrc);
        TextView srcNameTxt = findViewById(R.id.txt_srcname_mainplayer);
        TextView songTitleTxt = findViewById(R.id.txt_title_mainplayer);
        TextView artistTxt = findViewById(R.id.txt_artist_mainplayer);
        TextView songTimeTxt = findViewById(R.id.txt_songtime_mainplayer);
        TextView songDurationTxt = findViewById(R.id.txt_songduration_mainplayer);
        songSeekBar = findViewById(R.id.seekbar_mainplayer);
        ImageView artwork = findViewById(R.id.img_cover_mainplayer);
        Button playBtn = findViewById(R.id.btn_play_mainplayer);
        Button skipBtn = findViewById(R.id.btn_skip_mainplayer);
        Button rewindBtn = findViewById(R.id.btn_rewind_mainplayer);
        ConstraintLayout layout = findViewById(R.id.player_layout);

        songSeekBar.setProgress(0);
        playBtn.setEnabled(false);

        call.enqueue(new retrofit2.Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if (response.isSuccessful()) {
                    try {
                        track = response.body();
                        ///media player
                        try {
                            playBtn.setBackground(getDrawable(R.drawable.ic_pause));
                            mediaPlayer.setDataSource(track.file);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            isPlaying = true;
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage());
                        }
                        ///media player
                        int seconds = getDuration(track.file);
                        String second = "";
                        if (String.valueOf(seconds % 60).length() == 1) {
                            second = String.format(Locale.getDefault(), "0%d", seconds % 60);
                        } else {
                            second = String.valueOf(seconds % 60);
                        }
                        String str = String.format(Locale.getDefault(), "%d:%s", seconds / 60, second);
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
                        loaded.into(artwork, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                //todo uncomment
                                float shadow = 0.3F;
                                loaded.resize(500, 1000).centerCrop().transform(new BlurTransformation(PlayerActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        int color = StaticTools.getDominantColor(bitmap);
                                        color+=10010010;
                                        songSeekBar.setProgressTintList(ColorStateList.valueOf(color));
                                        songSeekBar.setThumbTintList(ColorStateList.valueOf(color));
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
                                StaticTools.LogErrorMessage(e.getMessage() + " image load");
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
            public void onFailure(Call<Track> call, Throwable t) {

            }
        });


        mediaPlayer.setOnPreparedListener(mp -> {
            int duration = mediaPlayer.getDuration();
            int amoungToupdate = duration / 100;
            Timer mTimer = new Timer();
            if (amoungToupdate > 0) {
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            songSeekBar.setMax(mediaPlayer.getDuration());
                            songSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                        });
                    }
                }, 0, amoungToupdate);
            }
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

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
                        int r = mediaPlayer.getCurrentPosition();
                        mediaPlayer.seekTo(progress);
                        mediaPlayer.start();
                    } catch (Exception e) {
                        StaticTools.LogErrorMessage(e.getMessage() + " seekbar change");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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
