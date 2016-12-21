package com.johnsoft.app.mymvp.model;

import java.io.RandomAccessFile;

import com.google.gson.Gson;
import com.johnsoft.app.mymvp.model.utils.SqliteHelper;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import okhttp3.OkHttpClient;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public interface IBaseModel {
    IBaseModel initialize();
    void destroy();
    Gson getGson();
    OkHttpClient getOkHttpClient();
    ContentResolver getContentResolver();
    SqliteHelper getDatabaseHelper();
    RandomAccessFile getFile(String path);
    SharedPreferences getSharedPreferences(String name);
}