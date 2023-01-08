package com.xizeyoupan.remoteclipboard.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;

@Slf4j
@Component
public class ConnectionInterceptor implements HandlerInterceptor {
    UserService userService;

    public ConnectionInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug("ConnectionInterceptor intercepts " + request.getRequestURI());
        HashMap<String, Object> map = new HashMap<>();

        Object _username = request.getSession().getAttribute("username");
        Object _password = request.getSession().getAttribute("password");

        String username = "", password = "";
        if (!ObjectUtils.isEmpty(_username) && !ObjectUtils.isEmpty(_password)) {
            username = _username.toString();
            password = _password.toString();
            User user = userService.getByUsername(username);
            if (!ObjectUtils.isEmpty(user) && user.getPassword().equals(password)) {
                return true;
            }
        }

        map.put("msg", "Invalid session.");
        log.error("Session error: username:" + username + ", password:" + password);

        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter writer = response.getWriter();
        String json = new ObjectMapper().writeValueAsString(map);
        writer.println(json);

        return false;
    }

}
