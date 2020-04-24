package com.mahaventures.wibe.di;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = PlayerModule.class)
public interface PlayerComponent {
    Player playerBuilder();
}
