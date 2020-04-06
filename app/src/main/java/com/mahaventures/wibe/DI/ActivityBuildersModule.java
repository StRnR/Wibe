package com.mahaventures.wibe.DI;

import com.mahaventures.wibe.Activities.TmpActivity;

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
