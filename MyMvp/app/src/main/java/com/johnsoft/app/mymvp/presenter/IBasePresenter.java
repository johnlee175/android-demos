package com.johnsoft.app.mymvp.presenter;

import android.content.Context;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public interface IBasePresenter {
    void onStart();
    void onStop();
    void onResume();
    void onPause();
    void onAttachedToWindow();
    void onDetachedFromWindow();
    void onWindowFocusChanged(boolean hasFocus);
    void onActivityContext(Context activity);
}
