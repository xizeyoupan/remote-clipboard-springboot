package com.xizeyoupan.boot.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.xizeyoupan.boot.bean.Clip;
import com.xizeyoupan.boot.bean.User;
import com.xizeyoupan.boot.service.TokenService;
import com.xizeyoupan.boot.service.UserService;
import com.xizeyoupan.boot.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;//孙旭鲁是我爸爸

import java.util.*;

@Slf4j
@Service
@Transactional
public class userServiceImpl implements UserService {

    @Autowired
    Cache<String, Object> caffeineCache;

    @Autowired
    User userTemplate;

    @Autowired
    TokenService tokenService;

    @Override
    public User getUserByName(String username, String password) {
        User user = (User) caffeineCache.getIfPresent(Util.keyForUser(username));
        if (user == null) {
            String token = tokenService.getToken(username, String.valueOf(0), password);
            userTemplate.setUsername(username);
            userTemplate.setToken(token);
            userTemplate.setConnectionNumber(0);
            caffeineCache.put(Util.keyForClips(username), new ArrayList<Clip>());
            caffeineCache.put(Util.keyForPassword(username), password);
            caffeineCache.put(Util.keyForUser(username), userTemplate);
        } else {
            if (Objects.equals(caffeineCache.getIfPresent(Util.keyForPassword(username)), password)) {
                userTemplate.setUsername(username);
                userTemplate.setConnectionNumber(user.getConnectionNumber() + 1);
                userTemplate.setToken(user.getToken());
            } else {
                return null;
            }
        }
        return userTemplate;
    }

    @Override
    public User deleteConnectionUser(User user) {

        return null;
    }
}
