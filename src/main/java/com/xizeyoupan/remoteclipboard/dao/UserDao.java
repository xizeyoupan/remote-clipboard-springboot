package com.xizeyoupan.remoteclipboard.dao;

import com.xizeyoupan.remoteclipboard.entity.User;

public interface UserDao {
    User getByUsername(String username);
    void save(User user);
}
