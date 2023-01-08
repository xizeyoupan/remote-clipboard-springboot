package com.xizeyoupan.remoteclipboard.controller.v1;

import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.oss.UcloudConfig;
import com.xizeyoupan.remoteclipboard.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private UserService userService;
    private UcloudConfig ucloudConfig;

    public UserController(UserService userService, UcloudConfig ucloudConfig) {
        this.userService = userService;
        this.ucloudConfig = ucloudConfig;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user, HttpSession session) {
        Map<String, Object> map = new HashMap<>();

        if (ObjectUtils.isEmpty(user) || ObjectUtils.isEmpty(user.getPassword()) || ObjectUtils.isEmpty(user.getUsername())) {
            map.put("msg", "Are you posting air or something else?");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        try {
            User findUser = userService.getByUsername(user.getUsername());
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
            if (ObjectUtils.isEmpty(findUser)) {
                map.put("msg", "No such user.");
                return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
            } else if (!findUser.getPassword().equals(user.getPassword())) {
                map.put("msg", "Incorrect password.");
                return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
            }

        } catch (RuntimeException e) {
            map.put("msg", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        map.put("msg", "Success");
        session.setAttribute("username", user.getUsername());
        session.setAttribute("password", user.getPassword());
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> map = new HashMap<>();

        if (ObjectUtils.isEmpty(user.getPassword()) || ObjectUtils.isEmpty(user.getUsername())) {
            map.put("msg", "Are you posting air or something else?");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        User foundUser = userService.getByUsername(user.getUsername());
        if (!ObjectUtils.isEmpty(foundUser)) {
            map.put("msg", "User already exists.");
            return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
        }

        try {
            userService.add(user);
        } catch (RuntimeException e) {
            map.put("msg", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        map.put("msg", "Success");
        return new ResponseEntity<>(map, HttpStatus.CREATED);

    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", ucloudConfig);
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

}
