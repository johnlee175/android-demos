package com.johnsoft.app.mymvp.view;

import com.johnsoft.app.mymvp.presenter.IBasePresenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected final PresenterComponent presenterComponent = DaggerPresenterComponent.create();

    public abstract IBasePresenter getPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onActivityContext(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onActivityContext(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onAttachedToWindow();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onDetachedFromWindow();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final IBasePresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onWindowFocusChanged(hasFocus);
        }
    }
}
