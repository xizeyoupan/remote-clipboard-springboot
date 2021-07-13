package com.xizeyoupan.boot.controller;

import com.xizeyoupan.boot.bean.RespBean;
import com.xizeyoupan.boot.bean.User;
import com.xizeyoupan.boot.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user")
    public Object creatOrGetUser(HttpServletResponse response, String username, String password) {

        if (password == null || null == username || username.equals("") || password.equals("")) {
            return new RespBean(-1, "Are you posting air or sth else?", null);
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        User user = userService.getUserByName(username, password);

        if (user == null) {
            return new RespBean(-1, "……隻能説，妳這個密碼錯了妳知道嗎ベΔ", null);
        } else {
            return new RespBean(200, "找到板子了，你是连接这个板子的的第" + user.getConnectionNumber() + "位火伴！", user);
        }

    }


}
