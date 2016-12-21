package com.johnsoft.app.mymvp.model.utils;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.johnsoft.app.mymvp.App;
import com.johnsoft.app.mymvp.model.pojo.Score;
import com.johnsoft.app.mymvp.model.pojo.User;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class SqliteHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "domain.db";
    private static final int DATABASE_VERSION = 1;

    public static final SqliteHelper singleInstance = new SqliteHelper();

    private Dao<Score, Integer> mScoreIdDao;

    public SqliteHelper() {
        super(App.self(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Score.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Score.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public Dao<Score, Integer> getScoreDao() {
        if (mScoreIdDao == null) {
            try {
                mScoreIdDao = getDao(User.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return mScoreIdDao;
    }
}
