package com.mahaventures.wibe.tools;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

public class PlayerHandler {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static boolean isPrepared;
    private static Drawable artwork;

    public static Drawable getArtwork() {
        return artwork;
    }

    public static void setArtwork(Drawable artwork) {
        PlayerHandler.artwork = artwork;
    }

    public static boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public static void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public static boolean isPrepared() {
        return isPrepared;
    }

    public static void prepare(String url) throws Exception {
        isPrepared = false;
        mediaPlayer.reset();
        mediaPlayer.setDataSource(url);
        mediaPlayer.setOnPreparedListener(mp -> {
            prepared();
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                }
//            }, 0, 500);
        });
        mediaPlayer.prepareAsync();
    }

    public static int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    private static void prepared() {
        isPrepared = true;
        mediaPlayer.start();
    }

    public static void pause() throws Exception {
        if (isPrepared)
            mediaPlayer.pause();
        else throw new Exception("PlayerHandler is not prepared");
    }

    public static void start() throws Exception {
        if (isPrepared) {
            mediaPlayer.start();
        } else throw new Exception("media player is not prepared yet");
    }

    public static void stop() {
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            throw e;
        }
    }

    public static int getDuration() {
        return mediaPlayer.getDuration();
    }

    public static void reset() {
        mediaPlayer.reset();
    }
}
