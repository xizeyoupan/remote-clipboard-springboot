package com.xizeyoupan.remoteclipboard.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xizeyoupan.remoteclipboard.utils.TokenUtil;
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

    final TokenUtil tokenUtil;

    public ConnectionInterceptor(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug("ConnectionInterceptor intercepts " + request.getRequestURI());

        HashMap<String, Object> map = new HashMap<>();

        String token;
        try {
            String authorization = request.getHeader("Authorization");

            token = authorization.split(" ")[1];
        } catch (Exception e) {
            map.put("msg", "invalid token");
            log.error("Token get error " + e);
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            PrintWriter writer = response.getWriter();
            String json = new ObjectMapper().writeValueAsString(map);
            writer.println(json);
            return false;
        }

        assert ObjectUtils.isEmpty(tokenUtil);
        if (tokenUtil.validToken(token)) {
            return true;
        } else {
            map.put("msg", "invalid token");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            PrintWriter writer = response.getWriter();
            String json = new ObjectMapper().writeValueAsString(map);
            writer.println(json);
            return false;
        }

    }
}
