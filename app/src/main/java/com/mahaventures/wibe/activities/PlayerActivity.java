package com.mahaventures.wibe.activities;

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
import android.media.MediaMetadataRetriever;
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

import com.mahaventures.wibe.R;
import com.mahaventures.wibe.fragments.MiniPlayerFragment;
import com.mahaventures.wibe.interfaces.Playable;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.services.CreateNotificationService;
import com.mahaventures.wibe.services.OnClearFromRecentService;
import com.mahaventures.wibe.tools.AlphaTransformation;
import com.mahaventures.wibe.tools.OnSwipeTouchListener;
import com.mahaventures.wibe.tools.StaticTools;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class PlayerActivity extends AppCompatActivity implements Playable {

    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static Bitmap artWork;
    private ImageView img;
    public static String mArtistString;
    public static String mTrackNameString;
    private static Track track;
    private int pos;
    private SeekBar songSeekBar;
    private boolean isPlaying;
    private Button playBtn;
    private Button addBtn;
    private Button minimizeBtn;
    private TextView srcTxt;
    private NotificationManager notificationManager;
    private TextView songDurationTxt;
    private TextView songTimeTxt;
    private static boolean isPrepared;
    public static int progressPosition;
    public static int maxProgress;
    public static List<Track> queue;
    private ImageView artwork;
    private ConstraintLayout layout;
    public static int trackNumber;
    private TextView songTitleTxt;
    private TextView artistTxt;
    public static boolean repeated;
    public static boolean shuffle;
    public static boolean meta;
    public static String from;

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        MiniPlayerFragment.isPrepared = true;
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
        meta = false;
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

        srcTxt = findViewById(R.id.txt_srcname_mainplayer);
        TextView srcNameTxt = findViewById(R.id.txt_srcname_mainplayer);
        songTitleTxt = findViewById(R.id.txt_title_mainplayer);
        artistTxt = findViewById(R.id.txt_artist_mainplayer);
        songTimeTxt = findViewById(R.id.txt_songtime_mainplayer);
        songDurationTxt = findViewById(R.id.txt_songduration_mainplayer);
        songSeekBar = findViewById(R.id.seekbar_mainplayer);
        artwork = findViewById(R.id.img_cover_mainplayer);
        playBtn = findViewById(R.id.btn_play_mainplayer);
        addBtn = findViewById(R.id.btn_add_player);
        playBtn.setBackground(getDrawable(R.drawable.ic_pause));
        Button skipBtn = findViewById(R.id.btn_skip_mainplayer);
        Button rewindBtn = findViewById(R.id.btn_rewind_mainplayer);
        layout = findViewById(R.id.player_layout);
        minimizeBtn = findViewById(R.id.btn_minimize_player);
        Button shuffleBtn = findViewById(R.id.btn_shuffle_player);
        Button repeatBtn = findViewById(R.id.btn_repeat_player);

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

        final View secondParent = (View) addBtn.getParent();
        secondParent.post(() -> {
            final Rect rect = new Rect();
            addBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            secondParent.setTouchDelegate(new TouchDelegate(rect, addBtn));
        });

        final View thirdParent = (View) shuffleBtn.getParent();
        thirdParent.post(() -> {
            final Rect rect = new Rect();
            shuffleBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            thirdParent.setTouchDelegate(new TouchDelegate(rect, shuffleBtn));
        });

        final View fourthParent = (View) repeatBtn.getParent();
        fourthParent.post(() -> {
            final Rect rect = new Rect();
            repeatBtn.getHitRect(rect);
            rect.top -= 50;
            rect.left -= 50;
            rect.bottom += 50;
            rect.right += 50;
            fourthParent.setTouchDelegate(new TouchDelegate(rect, repeatBtn));
        });

        shuffleBtn.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                shuffle = !shuffle;
                shuffleBtn.setBackgroundResource(shuffle ? R.drawable.ic_random_blue : R.drawable.ic_random);
                if (!shuffle) {
                    mediaPlayer.stop();
                    Collections.shuffle(queue);
                    firstOfAll();
                    doShit(0);
                }
            }
        });

        repeatBtn.setOnClickListener(v -> {
            if (repeated)
                repeatBtn.setBackgroundResource(R.drawable.ic_repeat);
            else
                repeatBtn.setBackgroundResource(R.drawable.ic_repeat_blue);
            repeated = !repeated;
        });

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

        minimizeBtn.setOnClickListener(v -> PlayerActivity.this.onBackPressed());

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isPrepared) {
                    try {
                        pos = progress;
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

        addBtn.setOnClickListener(v -> {
            addBtn.setBackgroundResource(R.drawable.ic_added);
            StaticTools.addToMySong(PlayerActivity.this, queue.get(trackNumber).id);
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
        if (from != null) {
            srcTxt.setText(from);
        }
        songDurationTxt.setText("");
        songTimeTxt.setText("");
        if (mediaPlayer != null && isPrepared)
            mediaPlayer.seekTo(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setMeta();
        super.onWindowFocusChanged(hasFocus);
    }

    private void setMeta() {
        if (track == null)
            return;
        String artist = StaticTools.getArtistsName(track);
        mArtistString = artist;
        artistTxt.setText(artist);
        String trackName = track.name;
        mTrackNameString = trackName;
        songTitleTxt.setText(trackName);
        meta = true;
        MiniPlayerFragment.isPrepared = true;
    }

    private void doShit(int i) {
        try {
            playBtn.setEnabled(false);
            mediaPlayer.release();
            meta = false;
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

            new Thread(() -> runOnUiThread(() -> {
                RequestCreator loaded = Picasso.get().load(track.image.medium.url);
                StaticTools.LogTimedMessage("loaded image");
                loaded.into(artwork, new Callback() {
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
                        layout.setBackgroundColor(getColor(R.color.darkBackground));
                        img = new ImageView(PlayerActivity.this);
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
            })).start();

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

    private void playMedia() {
        try {
            StaticTools.LogTimedMessage("play button pressed");
            isPlaying = true;
            MiniPlayerFragment.isPlaying = true;
            MiniPlayerFragment.isLoaded = true;
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
                                }
                                int pos = mediaPlayer.getCurrentPosition();
                                songSeekBar.setProgress(pos);
                                progressPosition = pos;
                                songTimeTxt.setText(StaticTools.getSongDuration(pos / 1000));
                            } catch (Exception e) {
                                StaticTools.LogErrorMessage(e.getMessage());
                            }
                        });
                    }
                }, 0, 500);
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(mp -> {
                if (repeated) {
                    if (isPrepared) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        pos = 0;
                    }
                } else next();
            });
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

    private int getDuration(String url) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(url);
        String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long dur = Long.parseLong(duration);
        return (int) dur;
    }

    private void previous() {
        pos = 0;
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
        pos = 0;
        firstOfAll();
        if (trackNumber < queue.size() - 1) {
            trackNumber++;
            doShit(trackNumber);
        } else {
//            pauseMedia();
//            PlayerActivity.this.onBackPressed();
        }
    }

    private void pauseMedia() {
        StaticTools.LogTimedMessage("pause button pressed");
        isPlaying = false;
        MiniPlayerFragment.isPlaying = false;
        onTrackPause();
        playBtn.setBackground(getDrawable(R.drawable.ic_play));
        pos = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
    }

    private BroadcastReceiver miniPlayerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = Objects.requireNonNull(intent.getExtras()).getString("action_name");
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

    private BroadcastReceiver playSongBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = Objects.requireNonNull(intent.getExtras()).getString("play_song_action");
            if (action != null && action.equals("pay")) {
//                PlayerActivity.this.finish();
                PlayerActivity.this.finish();
                MiniPlayerFragment.isLoaded = false;
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = Objects.requireNonNull(intent.getExtras()).getString("actionname");
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

    public static String getBlurredArtWork() {
        if (track != null) {
            return track.image.medium.url;
        }
        return "";
    }

}
