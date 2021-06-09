package com.xizeyoupan.boot.service;

import com.xizeyoupan.boot.bean.User;

public interface UserService {
    User getUserByName(String name,String password);

    User deleteConnectionUser(User user);
}
