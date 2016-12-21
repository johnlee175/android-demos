package com.johnsoft.app.mymvp.model.impl;

import java.io.RandomAccessFile;

import com.google.gson.Gson;
import com.johnsoft.app.mymvp.model.IBaseModel;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import okhttp3.OkHttpClient;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public abstract class AbstractBaseService implements IBaseModel {
    @Override
    public Gson getGson() {
        return null;
    }

    @Override
    public OkHttpClient getOkHttpClient() {
        return null;
    }

    @Override
    public ContentResolver getContentResolver() {
        return null;
    }

    @Override
    public SQLiteOpenHelper getDatabaseHelper() {
        return null;
    }

    @Override
    public RandomAccessFile getFile(String path) {
        return null;
    }

    @Override
    public SharedPreferences getSharedPreferences(String name) {
        return null;
    }
}
