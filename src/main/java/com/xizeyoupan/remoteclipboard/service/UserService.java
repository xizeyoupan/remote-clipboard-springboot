package com.xizeyoupan.remoteclipboard.service;

import com.xizeyoupan.remoteclipboard.entity.User;

public interface UserService {

    User add(User user);

    User getByUsername(String username);

}
