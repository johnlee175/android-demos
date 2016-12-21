package com.johnsoft.app.mymvp.view.protocol;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public interface IView {
    interface IViewCallback {
        void onAttach();
        void onDetach();
        void onEnterSession();
        void onExitSession();
    }

    void registerCallback(IViewCallback callback);
    void unregisterCallback(IViewCallback callback);
}
