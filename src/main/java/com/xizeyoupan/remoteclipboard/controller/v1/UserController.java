package com.xizeyoupan.remoteclipboard.controller.v1;

import com.xizeyoupan.remoteclipboard.entity.Connection;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.SseService;
import com.xizeyoupan.remoteclipboard.service.UserService;
import com.xizeyoupan.remoteclipboard.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {
    final UserService userService;
    final SseService sseService;


    public UserController(UserService userService, SseService sseService) {
        this.userService = userService;
        this.sseService = sseService;
    }

    @PostMapping("/users")
    public ResponseEntity<Map<java.lang.String, Object>> login(@RequestBody User user) {

        Map<java.lang.String, Object> map = new HashMap<>();
        if (ObjectUtils.isEmpty(user.getPassword()) || ObjectUtils.isEmpty(user.getUsername())) {
            map.put("msg", "Are you posting air or sth else?");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        User loginUser;
        try {
            loginUser = userService.save(user);
        } catch (RuntimeException e) {
            map.put("smg", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        int connectionId = loginUser.getTot();
        Connection connection = sseService.login(new Connection(loginUser.getUsername(), connectionId, 0, TokenUtil.getToken(loginUser, connectionId)));
        map.put("msg", "success");
        map.put("connection", connection);

        return new ResponseEntity<>(map, HttpStatus.CREATED);

    }

}
