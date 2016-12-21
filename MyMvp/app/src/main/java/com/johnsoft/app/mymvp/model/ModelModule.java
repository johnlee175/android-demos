package com.johnsoft.app.mymvp.model;

import javax.inject.Singleton;

import com.johnsoft.app.mymvp.model.impl.ScoreService;
import com.johnsoft.app.mymvp.model.impl.UserService;

import dagger.Module;
import dagger.Provides;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

@Module
public class ModelModule {
    @Singleton
    @Provides
    public IUserModel provideIUserModel() {
        return new UserService().initialize();
    }

    @Singleton
    @Provides
    public IScoreModel provideIScoreModel() {
        return new ScoreService().initialize();
    }
}
