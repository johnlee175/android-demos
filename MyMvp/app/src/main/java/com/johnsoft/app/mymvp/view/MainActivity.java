package com.johnsoft.app.mymvp.view;

import javax.inject.Inject;

import com.johnsoft.app.mymvp.R;
import com.johnsoft.app.mymvp.presenter.IRoutePresenter;

import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends BaseActivity {
    private IRoutePresenter routePresenter;

    @Inject
    public void setPresenter(IRoutePresenter routePresenter) {
        this.routePresenter = routePresenter;
    }

    @Override
    public IRoutePresenter getPresenter() {
        return routePresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterComponent.inject(this);
        String uri = "";
        if (savedInstanceState != null) {
            uri = savedInstanceState.getString("uri", "");
        }
        setContentView(routePresenter.route(uri));
        ListView listView = (ListView)findViewById(R.id.users);
        UserAdapter adapter = new UserAdapter(this);
        presenterComponent.inject(adapter);
        listView.setAdapter(adapter);
    }
}
