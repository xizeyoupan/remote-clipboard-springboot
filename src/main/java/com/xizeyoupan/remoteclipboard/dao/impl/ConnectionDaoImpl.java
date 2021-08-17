package com.xizeyoupan.remoteclipboard.dao.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.xizeyoupan.remoteclipboard.dao.ConnectionDao;
import com.xizeyoupan.remoteclipboard.entity.Connection;
import com.xizeyoupan.remoteclipboard.utils.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectionDaoImpl implements ConnectionDao {

    private final Cache<java.lang.String, Object> caffeineCache;

    @Autowired
    public ConnectionDaoImpl(Cache<java.lang.String, Object> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public void save(Connection connection) {
        caffeineCache.put(KeyGenerator.keyForConnection(connection.getUsername(), connection.getId()), connection);
    }

    @Override
    public Connection getByUsernameAndConnectionId(java.lang.String username, int id) {
        return (Connection) caffeineCache.getIfPresent(KeyGenerator.keyForConnection(username, id));
    }
}
