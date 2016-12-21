package com.johnsoft.app.mymvp.model.impl;

import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.johnsoft.app.mymvp.model.IScoreModel;
import com.johnsoft.app.mymvp.model.pojo.Score;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class ScoreService extends AbstractBaseService implements IScoreModel {
    @Override
    public ScoreService initialize() {
        return this;
    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean saveScoreToSqlite(Score score) {
        return false;
    }

    @Override
    public List<Score> parseScoreJson(String json) {
        return getGson().fromJson(json, new TypeToken<List<Score>>(){}.getType());
    }

    @Override
    public List<Score> getAllScoreFromSqlite() {
        return null;
    }

    @Override
    public List<Score> searchScoreFromSqliteByTerm(String term) {
        return null;
    }

    @Override
    public List<Score> searchScoreFromSqliteByUserId(String userId) {
        return null;
    }

    @Override
    public Score searchScoreFromSqliteBy(String userId, String term) {
        return null;
    }
}
