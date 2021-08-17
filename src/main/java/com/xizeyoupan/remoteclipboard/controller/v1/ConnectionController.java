package com.xizeyoupan.remoteclipboard.controller.v1;

import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.SseService;
import com.xizeyoupan.remoteclipboard.service.UserService;
import com.xizeyoupan.remoteclipboard.utils.KeyGenerator;
import com.xizeyoupan.remoteclipboard.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/connections")
public class ConnectionController {

    SseService sseService;
    UserService userService;

    public ConnectionController(SseService sseService, UserService userService) {
        this.sseService = sseService;
        this.userService = userService;
    }

    /**
     * @param token
     * @return
     * @throws IOException
     */
    @GetMapping("/timeline")
    public SseEmitter getTimeline(@RequestHeader("Authorization") String token) throws IOException {
        token = token.split(" ")[1];
        String username = TokenUtil.getUsernameByToken(token);
        Integer connectionId = TokenUtil.getConnectionIdByToken(token);

        User user = userService.getByUsername(username);
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitter.send(userService.getDistinctClips(user));

        sseService.addSseEmitter(KeyGenerator.keyForConnection(username, connectionId), sseEmitter);

        return sseEmitter;
    }

    @GetMapping("/close")
    public void closeSse(@RequestHeader("Authorization") String token) {
        String username = TokenUtil.getUsernameByToken(token.split(" ")[1]);
        sseService.delSseEmitterForUser(username);
    }

}
