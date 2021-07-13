package com.xizeyoupan.boot.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.xizeyoupan.boot.bean.RespBean;
import com.xizeyoupan.boot.config.CacheConfig;
import com.xizeyoupan.boot.service.UserService;
import com.xizeyoupan.boot.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {
    @Autowired
    UserService userService;
    @Autowired
    Cache<String, Object> caffeineCache;

    RespBean respBean = new RespBean();

    @GetMapping("/user/{username}/clips")
    public Object getClips(@PathVariable String username) {
        respBean.setCode(200);
        respBean.setMsg("success");
        respBean.setData(caffeineCache.getIfPresent(Util.keyForClips(username)));
        return respBean;
    }
}
