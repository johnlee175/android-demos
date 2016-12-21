package com.johnsoft.app.mymvp.model.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */
@DatabaseTable(tableName = "score")
public class Score {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField(columnName = "score")
    public String score;
    @DatabaseField(columnName = "user_id", canBeNull = false)
    public String userId;
    @DatabaseField(columnName = "term")
    public String term;
}
