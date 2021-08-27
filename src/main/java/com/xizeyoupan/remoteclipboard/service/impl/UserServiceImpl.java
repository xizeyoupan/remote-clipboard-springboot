package com.xizeyoupan.remoteclipboard.service.impl;

import com.xizeyoupan.remoteclipboard.dao.UserDao;
import com.xizeyoupan.remoteclipboard.entity.Clip;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.UserService;
import com.xizeyoupan.remoteclipboard.utils.ClipMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User save(User user) {
        String password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(password);

        User foundUser = userDao.getByUsername(user.getUsername());
        if (ObjectUtils.isEmpty(foundUser)) {
            user.setTot(1);
            user.setTimeline(new ArrayList<>());
            userDao.save(user);
            return user;
        } else {

            if (foundUser.getPassword().equals(password)) {
                foundUser.setTot(foundUser.getTot() + 1);
                userDao.save(foundUser);
                return foundUser;
            } else {
                throw new RuntimeException("Wrong password.");
            }

        }
    }


    @Override
    public void addClip(String username, Clip clip) {
        User user = userDao.getByUsername(username);
        List<Clip> timeline = user.getTimeline();
        timeline.add(clip);

        user.setTimeline(timeline);
    }

    @Override
    public List<Clip> getDistinctClips(User user) {
        Map<String, Clip> clipMap = new HashMap<>();
        user.getTimeline().forEach(clip -> clipMap.put(clip.getUuid(), clip));
        ArrayList<Clip> clips = new ArrayList<>(clipMap.values());
        clips.removeIf(clip -> clip.getClipMode() == ClipMode.DELETED);
        clips.sort(Comparator.comparingLong(o -> o.getModifyTime().getTime()));
        return clips;
    }

    @Override
    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }
}
