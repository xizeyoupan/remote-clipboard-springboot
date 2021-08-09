package com.xizeyoupan.remoteclipboard.service;

import com.xizeyoupan.remoteclipboard.entity.User;

public interface UserService {
    void register(User user);

    User login(User user);
}
