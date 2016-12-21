package com.johnsoft.app.mymvp.model.impl;

import java.io.IOException;
import java.util.List;

import com.johnsoft.app.mymvp.model.IUserModel;
import com.johnsoft.app.mymvp.model.pojo.User;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public class UserService extends AbstractBaseService implements IUserModel {
    private static final String BASE_URL = "http://url/user/";
    private UserFetcher userFetcher;

    @Override
    public UserService initialize() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
        userFetcher = retrofit.create(UserFetcher.class);
        return this;
    }

    @Override
    public void destroy() {

    }

    @Override
    public User fetchUserFromNet(String userId) { // you should cache request result to local storage
        final Call<User> call = userFetcher.fetchUserFromNet(userId);
        try {
            final Response<User> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                System.err.println("fetchUserFromNet: " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> fetchUsersFromNet() { // you should cache request result to local storage
        final Call<List<User>> call = userFetcher.fetchUsersFromNet();
        try {
            final Response<List<User>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                System.err.println("fetchUserFromNet: " + response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface UserFetcher {
        @Headers({
                "Content-Type: application/json;charset=utf-8",
        })
        @GET("/search.php")
        Call<User> fetchUserFromNet(@Query("user_id") String userId);

        @Headers({
                "Content-Type: application/json;charset=utf-8",
        })
        @GET("/search.php")
        Call<List<User>> fetchUsersFromNet();
    }
}
