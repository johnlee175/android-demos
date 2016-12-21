package com.johnsoft.app.mymvp.presenter;

import com.johnsoft.app.mymvp.presenter.impl.ChartPresenterImpl;
import com.johnsoft.app.mymvp.presenter.impl.RoutePresenterImpl;
import com.johnsoft.app.mymvp.presenter.impl.UserScorePresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

@Module
public class PresenterModule {
    @Provides public IChartPresenter provideIChartPresenter() {
        return new ChartPresenterImpl().injectDependencies();
    }

    @Provides public IUserScorePresenter provideIUserScorePresenter() {
        return new UserScorePresenterImpl().injectDependencies();
    }

    @Provides public IRoutePresenter provideIRoutePresenter() {
        return new RoutePresenterImpl().injectDependencies();
    }
}
