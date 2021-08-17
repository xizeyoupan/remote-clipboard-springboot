package com.xizeyoupan.remoteclipboard.dao.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.xizeyoupan.remoteclipboard.dao.UserDao;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.utils.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDaoImpl implements UserDao {

    private final Cache<String, Object> caffeineCache;

    @Autowired
    public UserDaoImpl(Cache<String, Object> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public User getByUsername(String username) {
        return (User) caffeineCache.getIfPresent(KeyGenerator.keyForUser(username));
    }

    @Override
    public void save(User user) {
        caffeineCache.put(KeyGenerator.keyForUser(user.getUsername()), user);
    }


}
