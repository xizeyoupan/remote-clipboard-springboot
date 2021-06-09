package com.xizeyoupan.boot.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.xizeyoupan.boot.bean.Connection;
import com.xizeyoupan.boot.bean.User;
import com.xizeyoupan.boot.service.TokenService;
import com.xizeyoupan.boot.service.UserService;
import com.xizeyoupan.boot.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public User getUserByName(String name, String password) {
        User user = (User) caffeineCache.getIfPresent(name);
        String nameUuid = Util.getUUID(name);
        String userConnectionsUuid = Util.getUUID("connection" + name);
        List<Connection> connections = new ArrayList<>();
        if (user == null) {
            String token = tokenService.getToken(name, String.valueOf(0), password);
            connections.add(new Connection(0, token, new Date().getTime()));
            userTemplate.setUsername(name);
            userTemplate.setToken(token);
            userTemplate.setCurrentConnectionId(0);
            caffeineCache.put(userConnectionsUuid, connections);
            caffeineCache.put(nameUuid, password);
            caffeineCache.put(name, userTemplate);
        } else {
            if (Objects.equals(caffeineCache.getIfPresent(nameUuid), password)) {
                connections = (ArrayList<Connection>) caffeineCache.getIfPresent(userConnectionsUuid);
                long currentConnectionId = connections.get(connections.size() - 1).getId() + 1;
                String token = tokenService.getToken(name, String.valueOf(currentConnectionId), password);
                Connection connection = new Connection(currentConnectionId, token, new Date().getTime());
                connections.add(connection);
                userTemplate.setUsername(name);
                userTemplate.setCurrentConnectionId(currentConnectionId);
                userTemplate.setToken(token);
                caffeineCache.put(userConnectionsUuid, connections);
                caffeineCache.put(name, userTemplate);
            } else {
                return null;
            }
        }
        log.info(connections.toString());
        return userTemplate;
    }

    @Override
    public User deleteConnectionUser(User user) {

        return null;
    }
}
