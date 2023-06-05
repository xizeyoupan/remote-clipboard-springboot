package com.xizeyoupan.remoteclipboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xizeyoupan.remoteclipboard.entity.User;

public interface UserService extends IService<User> {

    User add(User user);

    User getByUsername(String username);

}
