package com.johnsoft.app.mymvp.view;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.johnsoft.app.mymvp.presenter.IUserScorePresenter;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */
public class UserAdapter extends BaseAdapter implements IUserScorePresenter.IUsersCallback {
    private final ArrayList<Pair<String, String>> usernames = new ArrayList<>();
    private final WeakReference<Context> context;

    public UserAdapter(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Inject
    public void setPresenter(IUserScorePresenter userPresenter) {
        userPresenter.fetchUsernamesAsync(this);
    }

    @Override
    public int getCount() {
        return usernames.size();
    }

    @Override
    public Pair<String, String> getItem(int position) {
        return usernames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context ctx = context.get();
        if (ctx == null) {
            return null;
        }
        if (convertView == null) {
            convertView = new TextView(ctx);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        final Pair<String, String> pair = getItem(position);
        ((TextView)convertView).setText(pair.second);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetailActivity.class);
                intent.putExtra("id", pair.first);
                ctx.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public void onUsernames(List<Pair<String, String>> usernames) {
        this.usernames.addAll(usernames);
        notifyDataSetChanged();
    }
}
