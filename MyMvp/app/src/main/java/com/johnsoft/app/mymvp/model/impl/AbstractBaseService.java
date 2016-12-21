package com.johnsoft.app.mymvp.model.impl;

import java.io.RandomAccessFile;

import com.google.gson.Gson;
import com.johnsoft.app.mymvp.model.IBaseModel;
import com.johnsoft.app.mymvp.model.utils.SqliteHelper;

import android.content.ContentResolver;
import android.content.SharedPreferences;
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
    public SqliteHelper getDatabaseHelper() {
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
