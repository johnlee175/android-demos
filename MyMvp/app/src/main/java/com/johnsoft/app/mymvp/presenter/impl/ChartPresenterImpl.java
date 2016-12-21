package com.johnsoft.app.mymvp.presenter.impl;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import org.rxbus.Subscribe;

import com.johnsoft.app.mymvp.model.IScoreModel;
import com.johnsoft.app.mymvp.model.IUserModel;
import com.johnsoft.app.mymvp.model.pojo.Score;
import com.johnsoft.app.mymvp.model.receiver.MyReceiver;
import com.johnsoft.app.mymvp.presenter.IChartPresenter;
import com.johnsoft.app.mymvp.view.bean.ChartBean;
import com.johnsoft.app.mymvp.view.protocol.IChartUi;

import android.support.annotation.Keep;
import android.util.Pair;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */
@Keep
public class ChartPresenterImpl extends AbstractBasePresenter implements IChartPresenter {
    private IUserModel userService;
    private IScoreModel scoreService;

    private WeakReference<IChartUi> chartUi;

    @Inject
    public void setUserService(IUserModel userService) {
        this.userService = userService;
    }

    @Inject
    public void setScoreService(IScoreModel scoreService) {
        this.scoreService = scoreService;
    }

    @Override
    public ChartPresenterImpl injectDependencies() {
        modelComponent.inject(this);
        return this;
    }

    @Override
    public void calcChartBean(String term, IChartUi ui) {
        this.chartUi = new WeakReference<>(ui);
        Observable.just(new Pair<>(term, chartUi))
                .observeOn(Schedulers.io())
                .map(new Func1<Pair<String, WeakReference<IChartUi>>, Pair<ChartBean, WeakReference<IChartUi>>>() {
                    @Override
                    public Pair<ChartBean, WeakReference<IChartUi>> call(Pair<String, WeakReference<IChartUi>> pair) {
                        final ChartBean chartBean = new ChartBean();
                        List<Score> scores = scoreService.searchScoreFromSqliteByTerm(pair.first);
                        Observable.from(scores).observeOn(Schedulers.io()).collect(new Func0<ChartBean>() {
                            @Override
                            public ChartBean call() {
                                return chartBean;
                            }
                        }, new Action2<ChartBean, Score>() {
                            @Override
                            public void call(ChartBean cb, Score score) {
                                cb.values.add(Integer.parseInt(score.score));
                                cb.catagories.add(userService.fetchUserFromNet(score.userId).getName());
                            }
                        }).subscribe();
                        return new Pair<>(chartBean, pair.second);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<ChartBean, WeakReference<IChartUi>>>() {
                    @Override
                    public void call(Pair<ChartBean, WeakReference<IChartUi>> pair) {
                        IChartUi chartUi = pair.second.get();
                        if (chartUi != null) {
                            chartUi.onChartBean(pair.first);
                        }
                    }
                });
    }

    @Subscribe(code = MyReceiver.PUSH_EVENT, scheduler = Subscribe.SCHEDULER_COMPUTE_POOL_THREAD)
    public void onPushEvent(String json) {
        IChartUi ui;
        if (chartUi != null && (ui = chartUi.get()) != null) {
            final ChartBean chartBean = new ChartBean();
            List<Score> scores = scoreService.parseScoreJson(json);
            Observable.from(scores).observeOn(Schedulers.io()).collect(new Func0<ChartBean>() {
                @Override
                public ChartBean call() {
                    return chartBean;
                }
            }, new Action2<ChartBean, Score>() {
                @Override
                public void call(ChartBean cb, Score score) {
                    cb.values.add(Integer.parseInt(score.score));
                    cb.catagories.add(userService.fetchUserFromNet(score.userId).getName());
                }
            }).subscribe();
            ui.onChartBean(chartBean);
        }
    }
}
