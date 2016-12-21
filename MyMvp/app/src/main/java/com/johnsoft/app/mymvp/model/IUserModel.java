package com.johnsoft.app.mymvp.model;

import java.util.List;

import com.johnsoft.app.mymvp.model.pojo.User;

/**
 * @author John Kenrinus Lee
 * @version 2016-12-21
 */

public interface IUserModel extends IBaseModel {
    User fetchUserFromNet(String userId);
    List<User> fetchUsersFromNet();
}
