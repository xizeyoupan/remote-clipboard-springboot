package com.xizeyoupan.remoteclipboard.controller.v1;

import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
            log.error(e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
        map.put("msg", "Success");
        session.setAttribute("username", user.getUsername());
        session.setAttribute("password", user.getPassword());
        session.setAttribute("id", user.getId());

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user, HttpServletResponse response) {
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


}
