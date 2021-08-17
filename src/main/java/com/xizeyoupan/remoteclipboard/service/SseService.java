package com.xizeyoupan.remoteclipboard.service;

import com.xizeyoupan.remoteclipboard.entity.Connection;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {

    Connection login(Connection connection);

    void addSseEmitter(String id, SseEmitter sseEmitter);

    boolean delSseEmitterForUser(String username);

    boolean sendMsg(String id, Object msg);

    void sendMsgForUser(String username, Object msg);

}
