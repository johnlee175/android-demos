package com.johnsoft.app.mymvp.presenter.impl;

import com.johnsoft.app.mymvp.R;
import com.johnsoft.app.mymvp.presenter.IRoutePresenter;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class RoutePresenterImpl extends AbstractBasePresenter implements IRoutePresenter {
    @Override
    public RoutePresenterImpl injectDependencies() {
        return this;
    }

    @Override
    public int route(String uri) {
        if (uri.equals("no-permission")) {
            return R.layout.activity_blank;
        } else {
            return R.layout.activity_main;
        }
    }
}
