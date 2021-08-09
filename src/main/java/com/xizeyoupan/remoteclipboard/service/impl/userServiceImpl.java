package com.xizeyoupan.remoteclipboard.service.impl;

import com.xizeyoupan.remoteclipboard.dao.UserDao;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.UserService;
import com.xizeyoupan.remoteclipboard.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class userServiceImpl implements UserService {
    UserDao userDao;

    @Autowired
    public userServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void register(User user) {
        user.setTot(1);
        user.setToken(TokenUtil.getToken(user));

        userDao.save(user);
    }

    @Override
    public User login(User user) {
        String password = DigestUtils.md5DigestAsHex(user.getHashPassword().getBytes(StandardCharsets.UTF_8));
        user.setHashPassword(password);
        User foundUser = userDao.getByUserName(user.getUserName());

        if (ObjectUtils.isEmpty(foundUser)) {
            register(user);
            return user;

        } else {
            if (user.getHashPassword().equals(foundUser.getHashPassword())) {
                foundUser.setTot(foundUser.getTot() + 1);

                foundUser.setToken(TokenUtil.getToken(foundUser));
                userDao.save(foundUser);
            } else {
                throw new RuntimeException("wrong password");
            }

        }

        return foundUser;
    }
}
