package com.johnsoft.app.mymvp.presenter.impl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.rxbus.Subscribe;

import com.bumptech.glide.Glide;
import com.johnsoft.app.mymvp.model.IScoreModel;
import com.johnsoft.app.mymvp.model.IUserModel;
import com.johnsoft.app.mymvp.model.pojo.Score;
import com.johnsoft.app.mymvp.model.pojo.User;
import com.johnsoft.app.mymvp.model.receiver.MyReceiver;
import com.johnsoft.app.mymvp.presenter.IUserScorePresenter;
import com.johnsoft.app.mymvp.view.bean.DetailInfo;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.Pair;
import android.widget.ImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */
@Keep
public class UserScorePresenterImpl extends AbstractBasePresenter  implements IUserScorePresenter {
    private IUserModel userService;
    private IScoreModel scoreService;

    private WeakReference<IConnectivityCallback> connectivityCallback;

    @Inject
    public void setUserService(IUserModel userService) {
        this.userService = userService;
    }

    @Inject
    public void setScoreService(IScoreModel scoreService) {
        this.scoreService = scoreService;
    }

    @Override
    public UserScorePresenterImpl injectDependencies() {
        modelComponent.inject(this);
        return this;
    }

    @Override
    public void setConnectivityCallback(IConnectivityCallback callback) {
        this.connectivityCallback = new WeakReference<>(callback);
    }

    @Override
    public void loadAvatar(String url, ImageView imageView) {
        Context activity = getActivityContext();
        if (activity != null) {
            Glide.with(activity).load(url).into(imageView);
        }
    }

    @Override
    public void fetchUsernamesAsync(final IUsersCallback callback) {
        WeakReference<IUsersCallback> callbackRef = new WeakReference<>(callback);
        final ArrayList<Pair<String, String>> useridnames = new ArrayList<>();
        Observable.just(callbackRef)
                .observeOn(Schedulers.io())
                .map(new Func1<WeakReference<IUsersCallback>, WeakReference<IUsersCallback>>() {
                    @Override
                    public WeakReference<IUsersCallback> call(WeakReference<IUsersCallback> callback) {
                        for (User user : userService.fetchUsersFromNet()) {
                            useridnames.add(new Pair<>(user.getId(), user.getName()));
                        }
                        return callback;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeakReference<IUsersCallback>>() {
                    @Override
                    public void call(WeakReference<IUsersCallback> callback) {
                        IUsersCallback usersCallback = callback.get();
                        if (usersCallback != null) {
                            usersCallback.onUsernames(useridnames);
                        }
                    }
                });
    }

    @Override
    public void fetchUserDetailAsync(final String userId, IDetailCallback callback) {
        WeakReference<IDetailCallback> callbackRef = new WeakReference<>(callback);
        final DetailInfo info = new DetailInfo();
        Observable.just(callbackRef)
                .observeOn(Schedulers.io())
                .map(new Func1<WeakReference<IDetailCallback>, WeakReference<IDetailCallback>>() {
                    @Override
                    public WeakReference<IDetailCallback> call(WeakReference<IDetailCallback> callback) {
                        User user = userService.fetchUserFromNet(userId);
                        List<Score> scores = scoreService.searchScoreFromSqliteByUserId(userId);
                        Score score = scores.get(scores.size() - 1);
                        info.name = user.getName();
                        info.description = user.getDescription();
                        info.avatar = user.getImage();
                        info.score = score.score;
                        info.term = score.term;
                        return callback;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeakReference<IDetailCallback>>() {
                    @Override
                    public void call(WeakReference<IDetailCallback> callback) {
                        IDetailCallback detailCallback = callback.get();
                        if (detailCallback != null) {
                            detailCallback.onLoad(info);
                        }
                    }
                });
    }

    @Override
    public void saveDetailInfoAsync(final DetailInfo info, IDetailCallback callback) {
        WeakReference<IDetailCallback> callbackRef = new WeakReference<>(callback);
        Observable.just(callbackRef)
                .observeOn(Schedulers.io())
                .map(new Func1<WeakReference<IDetailCallback>, Pair<WeakReference<IDetailCallback>, Boolean>>() {
                    @Override
                    public Pair<WeakReference<IDetailCallback>, Boolean> call(WeakReference<IDetailCallback> callback) {
                        Score score = scoreService.searchScoreFromSqliteBy(info.name, info.term);
                        score.score = info.score;
                        boolean result = scoreService.saveScoreToSqlite(score);
                        return new Pair<>(callback, result);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<WeakReference<IDetailCallback>, Boolean>>() {
                    @Override
                    public void call(Pair<WeakReference<IDetailCallback>, Boolean> pair) {
                        IDetailCallback detailCallback = pair.first.get();
                        if (detailCallback != null) {
                            detailCallback.onSaved(pair.second);
                        }
                    }
                });
    }

    @Subscribe(code = MyReceiver.CONNECTIVITY_EVENT, scheduler = Subscribe.SCHEDULER_CURRENT_THREAD)
    public void onConnectivityEvent(int type) {
        Observable.just(type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer type) {
                        IConnectivityCallback callback;
                        if (connectivityCallback != null && (callback = connectivityCallback.get()) != null) {
                            callback.onNetworkChanged(type);
                        }
                    }
                });
    }
}
