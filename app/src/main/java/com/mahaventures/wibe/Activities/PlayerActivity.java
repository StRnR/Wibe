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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mahaventures.wibe.Adapters.SongsRecyclerSearchAdapter;
import com.mahaventures.wibe.Fragments.MiniPlayerFragment;
import com.mahaventures.wibe.Interfaces.Playable;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.R;
import com.mahaventures.wibe.Services.CreateNotificationService;
import com.mahaventures.wibe.Services.OnClearFromRecentService;
import com.mahaventures.wibe.Tools.AlphaTransformation;
import com.mahaventures.wibe.Tools.OnSwipeTouchListener;
import com.mahaventures.wibe.Tools.StaticTools;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class PlayerActivity extends AppCompatActivity implements Playable {

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static Bitmap artWork;
    public static String mArtistString;
    public static String mTrackNameString;
    static Track track;
    int pos = 0;
    SeekBar songSeekBar;
    boolean isPlaying;
    Button playBtn;
    Button minimizeBtn;
    NotificationManager notificationManager;
    TextView songDurationTxt;
    TextView songTimeTxt;
    boolean isPrepared;
    public static int progressPosition;
    public static int maxProgress;
    public static List<Track> queue;
    ImageView artwork;
    ConstraintLayout layout;
    public static int trackNumber;
    TextView songTitleTxt;
    TextView artistTxt;
    int sd;
    int sp;
    boolean repeated;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StaticTools.LogTimedMessage("on create fuuuuuuuuuuuuuuuuuuuuuuuuuuck");
        MiniPlayerFragment.isLoaded = false;
        setContentView(R.layout.activity_player);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        isPrepared = false;
        MiniPlayerFragment.context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            notificationManager.cancelAll();
        }
        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
            if (miniPlayerBroadcastReceiver != null)
                unregisterReceiver(miniPlayerBroadcastReceiver);
            if (playSongBroadcastReceiver != null)
                unregisterReceiver(playSongBroadcastReceiver);
        } catch (Exception e) {
            StaticTools.LogErrorMessage("register fucked up");
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

        TextView srcTxt = findViewById(R.id.txt_playersrc);
        TextView srcNameTxt = findViewById(R.id.txt_srcname_mainplayer);
        songTitleTxt = findViewById(R.id.txt_title_mainplayer);
        artistTxt = findViewById(R.id.txt_artist_mainplayer);
        songTimeTxt = findViewById(R.id.txt_songtime_mainplayer);
        songDurationTxt = findViewById(R.id.txt_songduration_mainplayer);
        songSeekBar = findViewById(R.id.seekbar_mainplayer);
        artwork = findViewById(R.id.img_cover_mainplayer);
        playBtn = findViewById(R.id.btn_play_mainplayer);
        playBtn.setBackground(getDrawable(R.drawable.ic_pause));
        Button skipBtn = findViewById(R.id.btn_skip_mainplayer);
        Button rewindBtn = findViewById(R.id.btn_rewind_mainplayer);
        layout = findViewById(R.id.player_layout);
        minimizeBtn = findViewById(R.id.btn_minimize_player);
        Button shuffle = findViewById(R.id.btn_shuffle_player);
        Button repeat = findViewById(R.id.btn_repeat_player);


        //todo Arshia: range dokme shuffle o repeat o dorost kon
        shuffle.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                Collections.shuffle(queue);
                firstOfAll();
                doShit(0);
            }
        });

        repeat.setOnClickListener(v -> repeated = !repeated);

        setDragActions();

        skipBtn.setOnClickListener(v -> {
            next();
            setMeta();
        });
        rewindBtn.setOnClickListener(v -> {
            previous();
            setMeta();
        });

        firstOfAll();

        //play song
        doShit(trackNumber);

        final View parent = (View) minimizeBtn.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            minimizeBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            parent.setTouchDelegate(new TouchDelegate(rect, minimizeBtn));
        });

        int color = Color.rgb(255, 255, 255);
        songSeekBar.setProgressTintList(ColorStateList.valueOf(color));
        songSeekBar.setThumbTintList(ColorStateList.valueOf(color));

        songTitleTxt.setText(mTrackNameString);
        artistTxt.setText(mArtistString);
        StaticTools.LogTimedMessage("meta added");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            registerReceiver(miniPlayerBroadcastReceiver, new IntentFilter("MINI_PLAYER"));
            registerReceiver(playSongBroadcastReceiver, new IntentFilter("PLAY_SONG"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }


        playBtn.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                pauseMedia();
            } else {
                playMedia();
            }
        });

        minimizeBtn.setOnClickListener(v -> {
            PlayerActivity.this.onBackPressed();
        });

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser && sd != 0 && Math.abs(sd - sp) <= 500) {
                    if (repeated) {
                        if (isPrepared) {
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                        }
                    } else next();
                }
                if (fromUser && isPrepared) {
                    try {
                        mediaPlayer.seekTo(progress);
                        mediaPlayer.start();
                    } catch (Exception e) {
                        StaticTools.LogErrorMessage(e.getMessage() + " seekBar change");
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

    private void setDragActions() {
        layout.setOnTouchListener(new OnSwipeTouchListener(PlayerActivity.this) {
            public void onSwipeBottom() {
                PlayerActivity.this.onBackPressed();
            }
        });

        artwork.setOnTouchListener(new OnSwipeTouchListener(PlayerActivity.this) {
            public void onSwipeRight() {
                previous();
                setMeta();
            }

            public void onSwipeLeft() {
                next();
                setMeta();
            }

            public void onSwipeBottom() {
                PlayerActivity.this.onBackPressed();
            }
        });
    }

    private void firstOfAll() {
        songDurationTxt.setText("");
        songTimeTxt.setText("");
        songSeekBar.setProgress(0);
        playBtn.setEnabled(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setMeta();
        super.onWindowFocusChanged(hasFocus);
    }

    private void setMeta() {
        MiniPlayerFragment.isPrepared = true;
        String artist = StaticTools.getArtistsName(track);
        mArtistString = artist;
        artistTxt.setText(artist);
        String trackName = track.name;
        mTrackNameString = trackName;
        songTitleTxt.setText(trackName);
    }

    private void doShit(int i) {
        try {
            mediaPlayer.release();
            MiniPlayerFragment.isLoaded = false;
            track = queue.get(trackNumber);
            setMeta();
            songSeekBar.setProgress(0);
            StaticTools.LogTimedMessage("track set");
            MiniPlayerFragment.miniTrack = track;
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(track != null ? track.file : null);
                playMedia();
                playBtn.setEnabled(true);
            } catch (Exception e) {
                StaticTools.LogErrorMessage(e.getMessage());
            }


            new Thread(() -> {
                runOnUiThread(() -> {
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
                            layout.setBackgroundColor(Color.BLACK);
                            ImageView img = new ImageView(PlayerActivity.this);
                            loaded.resize(width, height).centerCrop().transform(new BlurTransformation(PlayerActivity.this, 6, 6)).transform(new AlphaTransformation(shadow)).into(img, new Callback() {
                                @Override
                                public void onSuccess() {
                                    layout.setBackgroundDrawable(img.getDrawable());
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            StaticTools.LogErrorMessage(e.getMessage() + " image load");
                        }
                    });
                });
            }).start();


        } catch (Exception e) {
            StaticTools.LogErrorMessage(e.getMessage() + " player error");
        }
    }

    @Override
    public void onTrackPrevious() {
        previous();
    }

    @Override
    public void onTrackPlay() {
        CreateNotificationService.createNotification(PlayerActivity.this, track,
                R.drawable.ic_pause_black_24dp, trackNumber, queue.size());
        isPlaying = true;
    }

    @Override
    public void onTrackPause() {
        CreateNotificationService.createNotification(PlayerActivity.this, track,
                R.drawable.ic_play_arrow_black_24dp, trackNumber, queue.size());
//        play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
//        title.setText(tracks.get(position).getTitle());
        isPlaying = false;
    }

    @Override
    public void onTrackNext() {
        next();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(miniPlayerBroadcastReceiver);
        unregisterReceiver(playSongBroadcastReceiver);
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
                firstOfAll();
                mediaPlayer.seekTo(pos);
                mediaPlayer.start();
                playBtn.setEnabled(true);
                int duration = mediaPlayer.getDuration();
                MiniPlayerFragment.isLoaded = true;
                songDurationTxt.setText(StaticTools.getSongDuration(duration / 1000));
                Timer mTimer = new Timer();
                AtomicInteger counter = new AtomicInteger();
                counter.set(0);
                mTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            try {
                                counter.getAndIncrement();
                                if (counter.get() < 2) {
                                    songSeekBar.setMax(duration);
                                    maxProgress = duration;
                                    sd = duration;
                                }
                                int pos = mediaPlayer.getCurrentPosition();
                                sp = pos;
                                songSeekBar.setProgress(pos);
                                progressPosition = pos;
                                songTimeTxt.setText(StaticTools.getSongDuration(pos / 1000));
                            } catch (Exception e) {

                            }
                        });
                    }
                }, 0, 500);
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            StaticTools.LogErrorMessage(e.getMessage() + " inja player activity");
            try {
                mediaPlayer.reset();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(track != null ? track.file : null);
                playMedia();
            } catch (Exception e1) {
                StaticTools.LogErrorMessage(e1.getMessage() + " kir khord dg");
            }
        }
    }

    private void previous() {
        firstOfAll();
        if (trackNumber > 0) {
            trackNumber--;
            doShit(trackNumber);
        } else {
            try {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            } catch (Exception e) {
                StaticTools.LogErrorMessage(e.getMessage() + " previous");
            }
        }
    }

    private void next() {
        firstOfAll();
        if (trackNumber < queue.size()) {
            trackNumber++;
            doShit(trackNumber);
        } else {
            pauseMedia();
            PlayerActivity.this.onBackPressed();
        }
    }

    public void pauseMedia() {
        StaticTools.LogTimedMessage("pause button pressed");
        isPlaying = false;
        MiniPlayerFragment.isPlaying = false;
        MiniPlayerFragment.isPrepared = true;
        onTrackPause();
        playBtn.setBackground(getDrawable(R.drawable.ic_play));
        pos = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
    }

    BroadcastReceiver miniPlayerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("action_name");
            if (action != null) {
                switch (action) {
                    case MiniPlayerFragment.ACTION_PLAY:
                        if (isPlaying) {
                            pauseMedia();
                        } else {
                            playMedia();
                        }
                        break;
                    case MiniPlayerFragment.ACTION_NEXT:
                        onTrackNext();
                        break;

                }
            }
        }
    };

    BroadcastReceiver playSongBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("play_song_action");
            if (action != null && action.equals(SongsRecyclerSearchAdapter.ACTION)) {
//                PlayerActivity.this.finish();
                PlayerActivity.this.finish();
                MiniPlayerFragment.isLoaded = false;
            }
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

    //todo fixing
    //🎀💔🤎💙💚❤💖💋🌹🌹🌹🌹🌹
}
