package com.johnsoft.app.mymvp.model.impl;

import java.sql.SQLException;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.johnsoft.app.mymvp.model.IScoreModel;
import com.johnsoft.app.mymvp.model.pojo.Score;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class ScoreService extends AbstractBaseService implements IScoreModel {
    private Dao<Score, Integer> scoreDao;

    @Override
    public ScoreService initialize() {
        scoreDao = getDatabaseHelper().getScoreDao();
        return this;
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean saveScoreToSqlite(Score score) {
        try {
            return scoreDao.update(score) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Score> parseScoreJson(String json) {
        return getGson().fromJson(json, new TypeToken<List<Score>>(){}.getType());
    }

    @Override
    public List<Score> getAllScoreFromSqlite() {
        try {
            return scoreDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Score> searchScoreFromSqliteByTerm(String term) {
        try {
            return scoreDao.queryForEq("term", term);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Score> searchScoreFromSqliteByUserId(String userId) {
        try {
            return scoreDao.queryForEq("user_id", userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Score searchScoreFromSqliteBy(String userId, String term) {
        // not implement yet
        return null;
    }
}
