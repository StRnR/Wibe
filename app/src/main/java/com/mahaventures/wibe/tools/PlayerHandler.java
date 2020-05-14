package com.mahaventures.wibe.tools;

import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerHandler {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static boolean isPrepared;
    private static int pos;

    public static boolean isPrepared() {
        return isPrepared;
    }

    public static int getPos() {
        return pos;
    }

    public static void prepare(String url) throws Exception {
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

    public static void pause() throws Exception {
        if (isPrepared)
            mediaPlayer.pause();
        else throw new Exception("PlayerHandler is not prepared");
    }

    public static void resume() throws Exception {
        if (isPrepared) {
            mediaPlayer.start();
        } else throw new Exception("media player is not prepared yet");
    }

    public static void stop() {
        mediaPlayer.stop();
    }
}
