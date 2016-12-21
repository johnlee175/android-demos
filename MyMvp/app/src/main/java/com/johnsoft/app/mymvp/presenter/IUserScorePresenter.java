package com.johnsoft.app.mymvp.presenter;

import java.util.List;

import com.johnsoft.app.mymvp.view.bean.DetailInfo;

import android.util.Pair;
import android.widget.ImageView;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public interface IUserScorePresenter extends IBasePresenter {
    interface IUsersCallback {
        void onUsernames(List<Pair<String, String>> useridnames);
    }

    interface IDetailCallback {
        void onLoad(DetailInfo info);
        void onSaved(boolean success);
    }

    interface IConnectivityCallback {
        void onNetworkChanged(int type);
    }

    void loadAvatar(String url, ImageView imageView);

    void setConnectivityCallback(IConnectivityCallback callback);

    void fetchUsernamesAsync(IUsersCallback callback);

    void fetchUserDetailAsync(String userId, IDetailCallback callback);

    void saveDetailInfoAsync(DetailInfo info, IDetailCallback callback);
}
