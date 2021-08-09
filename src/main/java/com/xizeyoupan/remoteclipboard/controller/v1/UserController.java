package com.xizeyoupan.remoteclipboard.controller.v1;

import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {

        Map<String, Object> map = new HashMap<>();
        if (ObjectUtils.isEmpty(user.getHashPassword()) || ObjectUtils.isEmpty(user.getUserName())) {
            map.put("msg", "Are you posting air or sth else?");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        User loginUser;
        try {
            loginUser = userService.login(user);
        } catch (RuntimeException e) {
            map.put("smg", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        map.put("msg", "success");
        map.put("token", loginUser.getToken());
        map.put("username", loginUser.getUserName());
        map.put("connectId", loginUser.getTot());

        return new ResponseEntity<>(map, HttpStatus.CREATED);

    }


}
