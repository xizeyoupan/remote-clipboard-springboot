package com.xizeyoupan.boot.service;

import com.xizeyoupan.boot.bean.User;

public interface UserService {
    User getUserByName(String name);

    User deleteUser(User user);
}
