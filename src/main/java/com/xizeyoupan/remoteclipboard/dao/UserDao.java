package com.xizeyoupan.remoteclipboard.dao;

import com.xizeyoupan.remoteclipboard.entity.User;

public interface UserDao {
    User getByUserName(String userName);
    void save(User user);
}
