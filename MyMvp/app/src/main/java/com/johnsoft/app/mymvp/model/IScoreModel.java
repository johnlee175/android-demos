package com.johnsoft.app.mymvp.model;

import java.util.List;

import com.johnsoft.app.mymvp.model.pojo.Score;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public interface IScoreModel extends IBaseModel {
    boolean saveScoreToSqlite(Score score);
    List<Score> parseScoreJson(String json);
    List<Score> getAllScoreFromSqlite();
    List<Score> searchScoreFromSqliteByTerm(String term);
    List<Score> searchScoreFromSqliteByUserId(String userId);
    Score searchScoreFromSqliteBy(String userId, String term);
}
