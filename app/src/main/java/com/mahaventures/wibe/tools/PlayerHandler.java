package com.mahaventures.wibe.tools;

import android.media.MediaPlayer;

public class PlayerHandler {
    static MediaPlayer mediaPlayer = new MediaPlayer();
    static boolean isPrepared;

    public static void setUrl(String url) throws Exception {
        isPrepared = false;
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(mp -> {
            prepared();
        });
        mediaPlayer.start();
    }

    private static void prepared() {
        isPrepared = true;
    }

    public static void stop() throws Exception {
        if (isPrepared)
            mediaPlayer.stop();
        else throw new Exception("PlayerHandler is not prepared");
    }
}
