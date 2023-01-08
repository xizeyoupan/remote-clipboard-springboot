package com.xizeyoupan.remoteclipboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xizeyoupan.remoteclipboard.dao.ClipDao;
import com.xizeyoupan.remoteclipboard.entity.Clip;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.ClipService;
import com.xizeyoupan.remoteclipboard.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClipServiceImpl implements ClipService {

    private ClipDao clipDao;
    private UserService userService;

    public ClipServiceImpl(ClipDao clipDao, UserService userService) {
        this.clipDao = clipDao;
        this.userService = userService;
    }

    @Override
    public Clip add(Clip clip) {
        clipDao.insert(clip);
        return clip;
    }

    @Override
    public Clip update(Clip clip) {
        QueryWrapper<Clip> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", clip.getUuid());
        clipDao.update(clip, queryWrapper);
        return clip;
    }

    @Override
    public List<Clip> getList(String username) {
        User user = userService.getByUsername(username);
        QueryWrapper<Clip> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        return clipDao.selectList(queryWrapper);
    }

    @Override
    public Clip delete(String uuid) {
        Clip clip = getByUuid(uuid);
        clipDao.deleteById(clip);
        return clip;
    }

    @Override
    public Integer deleteAll(String username) {
        User user = userService.getByUsername(username);
        QueryWrapper<Clip> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.eq("status", 1);
        return clipDao.delete(queryWrapper);
    }

    @Override
    public Clip getByUuid(String uuid) {
        QueryWrapper<Clip> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", uuid);
        Clip clip = clipDao.selectOne(queryWrapper);
        return clip;
    }
}
