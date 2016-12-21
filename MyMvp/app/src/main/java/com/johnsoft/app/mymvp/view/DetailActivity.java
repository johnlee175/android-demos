package com.johnsoft.app.mymvp.view;

import javax.inject.Inject;

import com.johnsoft.app.mymvp.R;
import com.johnsoft.app.mymvp.presenter.IUserScorePresenter;
import com.johnsoft.app.mymvp.view.bean.DetailInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class DetailActivity extends BaseActivity
        implements IUserScorePresenter.IDetailCallback, IUserScorePresenter.IConnectivityCallback {
    private IUserScorePresenter presenter;
    private DetailInfo info;

    private ImageView avatar;
    private TextView name;
    private TextView description;
    private TextView term;
    private TextView score;

    @Inject
    public void setPresenter(IUserScorePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IUserScorePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        presenterComponent.inject(this);
        avatar = (ImageView)findViewById(R.id.avatar);
        name = (TextView)findViewById(R.id.name);
        description = (TextView)findViewById(R.id.description);
        term = (TextView)findViewById(R.id.term);
        score = (TextView)findViewById(R.id.score);
        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info != null) {
                    String scoreValue = verify(score.getText().toString());
                    if (scoreValue == null) {
                        Toast.makeText(DetailActivity.this, "score invalid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    info.score = scoreValue;
                    presenter.saveDetailInfoAsync(info, DetailActivity.this);
                } else {
                    Toast.makeText(DetailActivity.this, "load info failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button vs = (Button)findViewById(R.id.vs);
        vs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ChartActivity.class);
                intent.putExtra("term", info.term);
                DetailActivity.this.startActivity(intent);
            }
        });
        presenter.fetchUserDetailAsync(getIntent().getStringExtra("id"), DetailActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setConnectivityCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.setConnectivityCallback(null);
    }

    private String verify(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
        return value;
    }

    @Override
    public void onLoad(DetailInfo info) {
        if (info != null) {
            this.info = info;
            name.setText(info.name);
            description.setText(info.description);
            term.setText(info.term);
            score.setText(info.score);
//            Glide.with(this).load(info.avatar).into(avatar); // it's ok
            presenter.loadAvatar(info.avatar, avatar); // it's also ok
        }
    }

    @Override
    public void onSaved(boolean success) {
        Toast.makeText(this, "save" + (success ? "success" : "failed"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkChanged(int type) {
        if (type == -1) {
            Toast.makeText(this, "network is not available", Toast.LENGTH_SHORT).show();
        }
    }
}
