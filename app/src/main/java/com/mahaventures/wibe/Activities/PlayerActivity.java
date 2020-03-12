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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Interfaces.Playable;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.CreateNotificationService;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Services.OnClearFromRecentService;
import com.mahaventures.wibe.Tools.AlphaTransformation;
import com.mahaventures.wibe.Tools.RetrofitClientInstance;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Response;

import static java.lang.Float.isNaN;

public class PlayerActivity extends AppCompatActivity implements Playable {

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static Bitmap artWork;
    public static String CUSTOM_BROADCAST_ACTION = "miniPlayerAction";
    public static String mArtistString;
    public static String mTrackNameString;
    static Track track;
    int pos = 0;
    SeekBar songSeekBar;
    boolean isPlaying;
    Button playBtn;
    NotificationManager notificationManager;
    TextView songDurationTxt;
    TextView songTimeTxt;
    boolean isPrepared;
    BroadcastReceiver miniPlayerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action_name");
            if (action != null && action.equals(MiniPlayerFragment.ACTION_PLAY)) {
                if (isPlaying) {
                    pauseMedia();
                } else {
                    playMedia();
                }
            }
            int a = 2;
        }
    };

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            if (action != null) {
                switch (action) {
                    case CreateNotificationService.ACTION_PREVIOUS:
                        onTrackPrevious();
                        break;
                    case CreateNotificationService.ACTION_PLAY:
                        if (isPlaying) {
                            pauseMedia();
                        } else {
                            playMedia();
                        }
                        break;
                    case CreateNotificationService.ACTION_NEXT:
                        onTrackNext();
                        break;
                }
            }
        }
    };

    public static String getTrackName() {
        return track != null ? track.name : "";
    }

    public static String getArtistsName() {
        return track != null ? StaticTools.getArtistsName(track) : "";
    }

    public static Bitmap getArtWork() {
        return artWork;
    }

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

        MiniPlayerFragment.context = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            notificationManager.cancelAll();
        }
        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }

        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                StaticTools.LogErrorMessage(e.getMessage());
            }
        }
        Call<Track> call = service.GetTrackById(url);
        TextView srcTxt = findViewById(R.id.txt_playersrc);
        TextView srcNameTxt = findViewById(R.id.txt_srcname_mainplayer);
        TextView songTitleTxt = findViewById(R.id.txt_title_mainplayer);
        TextView artistTxt = findViewById(R.id.txt_artist_mainplayer);
        songTimeTxt = findViewById(R.id.txt_songtime_mainplayer);
        songDurationTxt = findViewById(R.id.txt_songduration_mainplayer);
        songSeekBar = findViewById(R.id.seekbar_mainplayer);
        ImageView artwork = findViewById(R.id.img_cover_mainplayer);
        playBtn = findViewById(R.id.btn_play_mainplayer);
        playBtn.setBackground(getDrawable(R.drawable.ic_pause));
        Button skipBtn = findViewById(R.id.btn_skip_mainplayer);
        Button rewindBtn = findViewById(R.id.btn_rewind_mainplayer);
        ConstraintLayout layout = findViewById(R.id.player_layout);

        //todo uncomment
        songTitleTxt.setText(mTrackNameString);
        artistTxt.setText(mArtistString);
        StaticTools.LogTimedMessage("meta added");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            registerReceiver(miniPlayerBroadcastReceiver, new IntentFilter("MINI_PLAYER"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        songSeekBar.setProgress(0);
        playBtn.setEnabled(false);

        call.enqueue(new retrofit2.Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                StaticTools.LogTimedMessage("response received");
                if (response.isSuccessful()) {
                    try {
                        track = response.body();
                        StaticTools.LogTimedMessage("track set");
                        MiniPlayerFragment.miniTrack = track;
                        ///media player
                        try {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(track != null ? track.file : null);
//                            playBtn.setBackground(getDrawable(R.drawable.ic_pause));
//                            mediaPlayer.prepare();
//                            mediaPlayer.start();
//                            isPlaying = true;
                            playMedia();
                            playBtn.setEnabled(true);
                        } catch (Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage());
                        }
                        ///media player
                        RequestCreator loaded = Picasso.get().load(track.image.medium.url);
                        StaticTools.LogTimedMessage("loaded image");
                        loaded.into(artwork, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                StaticTools.LogTimedMessage("artwork bitmap loaded");
                                float shadow = 0.5F;
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int height = displayMetrics.heightPixels;
                                int width = displayMetrics.widthPixels;

                                loaded.into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        artWork = bitmap;
                                        MiniPlayerFragment.isPrepared = true;
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });

                                loaded.resize(width, height).centerCrop().transform(new BlurTransformation(PlayerActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        StaticTools.LogTimedMessage("big bitmap loaded");
                                        int color = StaticTools.getDominantColor(bitmap);
                                        int r = (color >> 16) & 0xFF;
                                        int g = (color >> 8) & 0xFF;
                                        int b = (color) & 0xFF;
                                        int min = Math.min(Math.min(r, g), b);
                                        int max = Math.max(Math.max(r, g), b);
                                        int v = max, h, s;
                                        int delta = (max - min > 0) ? max - min : 1;
                                        if (max != 0) {
                                            s = delta / max;
                                        } else {
                                            s = 0;
                                            h = -1;
                                        }

                                        if (r == max) {
                                            h = (g - b) / delta;
                                        } else if (g == max) {
                                            h = 2 + (b - r) / delta;
                                        } else {
                                            h = 4 + (r - g) / delta;
                                        }
                                        h *= 60;
                                        if (h < 0)
                                            h += 360;
                                        if (isNaN(h))
                                            h = 0;

                                        color = Color.rgb(h, s, v);
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
                if (fromUser && isPrepared) {
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

    @Override
    public void onTrackPrevious() {

    }

    @Override
    public void onTrackPlay() {
        CreateNotificationService.createNotification(PlayerActivity.this, track,
                R.drawable.ic_pause_black_24dp, 0, 0);
        isPlaying = true;
    }

    @Override
    public void onTrackPause() {
        CreateNotificationService.createNotification(PlayerActivity.this, track,
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
            NotificationChannel channel = new NotificationChannel(CreateNotificationService.CHANNEL_ID,
                    "Wibe", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void playMedia() {
        try {
            StaticTools.LogTimedMessage("play button pressed");
            isPlaying = true;
            MiniPlayerFragment.isPrepared = true;
            MiniPlayerFragment.isPlaying = true;
            onTrackPlay();
            playBtn.setBackground(getDrawable(R.drawable.ic_pause));
            mediaPlayer.setOnPreparedListener(mp -> {
                StaticTools.LogTimedMessage("media player prepared");
                isPrepared = true;
                int duration = mediaPlayer.getDuration();
                songDurationTxt.setText(StaticTools.getSongDuration(duration / 1000));
                int amoungToupdate = duration / 500;
                Timer mTimer = new Timer();
                if (amoungToupdate > 0) {
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                try {
                                    songSeekBar.setMax(mediaPlayer.getDuration());
                                    songSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                                    songTimeTxt.setText(StaticTools.getSongDuration(mediaPlayer.getCurrentPosition() / 1000));
                                    if (mediaPlayer.getDuration() == mediaPlayer.getCurrentPosition()) {
                                        mediaPlayer.release();
                                        PlayerActivity.this.onBackPressed();
                                    }
                                } catch (Exception e) {

                                }
                            });
                        }
                    }, 0, amoungToupdate);
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.seekTo(pos);
            mediaPlayer.start();
        } catch (Exception e) {
            StaticTools.LogErrorMessage(e.getMessage() + " inja player activity");
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
        StaticTools.LogTimedMessage("pause button pressed");
        isPlaying = false;
        MiniPlayerFragment.isPlaying = false;
        onTrackPause();
        playBtn.setBackground(getDrawable(R.drawable.ic_play));
        pos = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
    }
}
