package com.johnsoft.app.mymvp.presenter.impl;

import java.lang.ref.WeakReference;

import org.rxbus.RxBus;

import com.johnsoft.app.mymvp.presenter.IBasePresenter;

import android.content.Context;
import android.support.annotation.Keep;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */
@Keep
public abstract class AbstractBasePresenter implements IBasePresenter {
    protected static final ModelComponent modelComponent = DaggerModelComponent.create();

    private WeakReference<Context> context;

    public abstract AbstractBasePresenter injectDependencies();

    protected final Context getActivityContext() {
        if (context == null) {
            return null;
        }
        return context.get();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {
        RxBus.singleInstance.registerSync(this);
    }

    @Override
    public void onPause() {
        RxBus.singleInstance.unregisterSync(this);
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDetachedFromWindow() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public void onActivityContext(Context activity) {
        if (activity == null) {
            context = null;
        } else {
            context = new WeakReference<>(activity);
        }
    }
}
