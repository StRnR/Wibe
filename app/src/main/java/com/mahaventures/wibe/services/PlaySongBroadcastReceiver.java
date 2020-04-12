package com.mahaventures.wibe.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlaySongBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("PLAY_SONG")
                .putExtra("play_song_action", intent.getAction()));
    }
}
