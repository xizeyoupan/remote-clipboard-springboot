package com.xizeyoupan.boot.controller;

import com.xizeyoupan.boot.bean.RespBean;
import com.xizeyoupan.boot.bean.User;
import com.xizeyoupan.boot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user")
    public Object creatOrGetUser(HttpServletResponse response, @RequestParam(value = "username") String username,
                                 @RequestParam(value = "password") String password) {

        User user = userService.getUserByName(username);
        if (user==null){

        }else {

        }
        return new RespBean(200,"get success",null);
    }
}
