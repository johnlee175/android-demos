package com.johnsoft.app.mymvp.presenter.impl;

import com.johnsoft.app.mymvp.model.ModelModule;

import dagger.Component;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

@Component(modules = ModelModule.class)
public interface ModelComponent {
    void inject(ChartPresenterImpl chartPresenter);
    void inject(UserScorePresenterImpl userScorePresenter);
}
