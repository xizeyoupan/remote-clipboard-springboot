package com.xizeyoupan.remoteclipboard.service.impl;

import com.xizeyoupan.remoteclipboard.dao.ConnectionDao;
import com.xizeyoupan.remoteclipboard.entity.Connection;
import com.xizeyoupan.remoteclipboard.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseServiceImpl implements SseService {
    private static final Map<String, SseEmitter> session = new ConcurrentHashMap<>();

    final ConnectionDao connectionDao;

    @Autowired
    public SseServiceImpl(ConnectionDao connectionDao) {
        this.connectionDao = connectionDao;
    }

    @Override
    public Connection login(Connection connection) {
        connectionDao.save(connection);
        return connection;
    }

    @Override
    public void addSseEmitter(String emitterName, SseEmitter sseEmitter) {
        session.put(emitterName, sseEmitter);
    }

    @Override
    public boolean delSseEmitterForUser(String username) {
        System.out.println("session.size() = " + session.size());
        session.entrySet().removeIf(entry -> {
            if (entry.getKey().split("/")[1].equals(username)) {
                entry.getValue().complete();
                return true;
            } else {
                return false;
            }
        });

        System.out.println("session.size() = " + session.size());

        return true;
    }

    @Override
    public boolean sendMsg(String emitterName, Object msg) {
        final SseEmitter emitter = session.get(emitterName);
        if (!ObjectUtils.isEmpty(emitter)) {
            try {
                emitter.send(msg);
                return true;
            } catch (IOException e) {
                log.error(e.toString());
                return false;
            }
        }
        return false;
    }

    @Override
    public void sendMsgForUser(String username, Object msg) {
        for (Map.Entry<String, SseEmitter> entry : session.entrySet()) {
            if (entry.getKey().split("/")[1].equals(username)) {
                try {
                    entry.getValue().send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.toString());
                    return;
                }
            }
        }

    }
}
