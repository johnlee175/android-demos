package com.johnsoft.app.mymvp.presenter;

import com.johnsoft.app.mymvp.view.protocol.IChartUi;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public interface IChartPresenter extends IBasePresenter {
    void calcChartBean(String term, IChartUi ui);
}
