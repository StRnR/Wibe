package com.mahaventures.wibe.di;

import android.media.MediaPlayer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlayerModule {
    @Provides
    @Singleton
    public MediaPlayer providePlayer() {
        return new MediaPlayer();
    }
}
