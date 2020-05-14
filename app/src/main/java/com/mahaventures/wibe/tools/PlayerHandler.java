package com.mahaventures.wibe.tools;

import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerHandler {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static boolean isPrepared;
    private static int pos;

    public static void setUrl(String url) throws Exception {
        isPrepared = false;
        mediaPlayer.setDataSource(url);
        mediaPlayer.setOnPreparedListener(mp -> {
            prepared();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getPosition();
                }
            }, 0, 500);
        });
        mediaPlayer.prepareAsync();
    }

    private static void getPosition() {
        pos = mediaPlayer.getCurrentPosition();
    }

    private static void prepared() {
        isPrepared = true;
        mediaPlayer.start();
    }

    public static void stop() throws Exception {
        if (isPrepared)
            mediaPlayer.stop();
        else throw new Exception("PlayerHandler is not prepared");
    }

    public static void start() throws Exception {
        if (isPrepared) {
            mediaPlayer.seekTo(pos);
            mediaPlayer.start();
        } else throw new Exception("media player is not prepared yet");
    }
}
