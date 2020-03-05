package com.mahaventures.wibe.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mahaventures.wibe.CreateNotification;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Playable;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Services.OnClearFromRecentService;
import com.mahaventures.wibe.Tools.AlphaTransformation;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity implements Playable {

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    static Track track;
    int pos = 0;
    SeekBar songSeekBar;
    boolean isPlaying;
    public static Bitmap artWork;
    Button playBtn;
    NotificationManager notificationManager;

    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        String trackId = getIntent().getStringExtra("id");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        String url = String.format("https://api.musicify.ir/tracks/%s?include=artists,album", trackId);
        //todo
        /////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            notificationManager.cancelAll();
        }
        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
        //////
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
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
        playBtn = findViewById(R.id.btn_play_mainplayer);
        playBtn.setBackground(getDrawable(R.drawable.ic_pause));
        Button skipBtn = findViewById(R.id.btn_skip_mainplayer);
        Button rewindBtn = findViewById(R.id.btn_rewind_mainplayer);
        ConstraintLayout layout = findViewById(R.id.player_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }


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
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(track != null ? track.file : null);

//                            playBtn.setBackground(getDrawable(R.drawable.ic_pause));
//                            mediaPlayer.prepare();
//                            mediaPlayer.start();
//                            isPlaying = true;
                            playMedia();
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
                        artistTxt.setText(StaticTools.getArtistsName(track));
                        playBtn.setEnabled(true);
                        RequestCreator loaded = Picasso.get().load(track.image.medium.url);
                        loaded.into(artwork, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                //todo uncomment
                                float shadow = 0.5F;
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int height = displayMetrics.heightPixels;
                                int width = displayMetrics.widthPixels;
                                loaded.resize(width, height).centerCrop().transform(new BlurTransformation(PlayerActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        artWork = bitmap;
                                        int color = StaticTools.getDominantColor(bitmap);
                                        int r = (color >> 16) & 0xFF;
                                        int g = (color >> 8) & 0xFF;
                                        int b = (color) & 0xFF;
                                        r = (255 - r) / 2;
                                        g = ((255 - g) * 3) / 5;
                                        b = ((255 - b) * 1) / 4;
                                        color = Color.rgb(r, g, b);
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

        playBtn.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                pauseMedia();
            } else {
                playMedia();
            }
        });

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    try {
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
        int timeInMilliSec = Integer.parseInt(time);
        int duration = timeInMilliSec / 1000;
        int hours = duration / 3600;
        int minutes = (duration - hours * 3600) / 60;
        int seconds = duration - (hours * 3600 + minutes * 60);
        return (minutes * 60) + seconds;
    }

    @Override
    public void onTrackPrevious() {

    }

    @Override
    public void onTrackPlay() {
        CreateNotification.createNotification(PlayerActivity.this, track,
                R.drawable.ic_pause_black_24dp, 0, 0);
        isPlaying = true;
    }

    @Override
    public void onTrackPause() {
        CreateNotification.createNotification(PlayerActivity.this, track,
                R.drawable.ic_play_arrow_black_24dp, 0, 0);
//        play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
//        title.setText(tracks.get(position).getTitle());
        isPlaying = false;
    }

    @Override
    public void onTrackNext() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
        mediaPlayer.release();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "Wibe", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            if (action != null) {
                switch (action) {
                    case CreateNotification.ACTION_PREVIOUS:
                        onTrackPrevious();
                        break;
                    case CreateNotification.ACTION_PLAY:
                        if (isPlaying) {
                            pauseMedia();
                        } else {
                            playMedia();
                        }
                        break;
                    case CreateNotification.ACTION_NEXT:
                        onTrackNext();
                        break;
                }
            }
        }
    };

    public void playMedia() {
        try {
            isPlaying = true;
            onTrackPlay();
            playBtn.setBackground(getDrawable(R.drawable.ic_pause));
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
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
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.seekTo(pos);
            mediaPlayer.start();
        } catch (Exception e) {
            StaticTools.LogErrorMessage(e.getMessage() + " inja");
            try {
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(track != null ? track.file : null);
                playMedia();
            } catch (Exception e1) {
                StaticTools.LogErrorMessage(e1.getMessage() + " kir khord dg");
            }
        }
    }

    public void pauseMedia() {
        isPlaying = false;
        onTrackPause();
        playBtn.setBackground(getDrawable(R.drawable.ic_play));
        pos = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
    }

    public static String getTrackName() {
        return track != null ? track.name : getTrackName();
    }

    public static String getArtistsName() {
        return track != null ? StaticTools.getArtistsName(track) : getArtistsName();
    }

    public static Bitmap getArtWork() {
        return artWork != null ? artWork : getArtWork();
    }


}
