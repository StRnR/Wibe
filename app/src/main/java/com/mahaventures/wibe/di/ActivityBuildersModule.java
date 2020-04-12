package com.mahaventures.wibe.di;

import com.mahaventures.wibe.activities.TmpActivity;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract TmpActivity contributeAuthActivity();


    @Provides
    static String someString() {
        return "this is a test string";
    }
}
