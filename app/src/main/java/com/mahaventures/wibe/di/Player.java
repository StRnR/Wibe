package com.mahaventures.wibe.di;

import android.media.MediaPlayer;

import javax.inject.Inject;

public class Player {
    private MediaPlayer player;

    @Inject
    public Player(MediaPlayer player) {
        this.player = player;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void stop() {
        player.stop();
    }
}
