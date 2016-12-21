package com.johnsoft.app.mymvp.view;

import com.johnsoft.app.mymvp.presenter.PresenterModule;

import dagger.Component;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */
@Component(modules = PresenterModule.class)
public interface PresenterComponent {
    void inject(MainActivity mainActivity);
    void inject(ChartActivity chartActivity);
    void inject(DetailActivity detailActivity);
    void inject(UserAdapter userAdapter);
}
