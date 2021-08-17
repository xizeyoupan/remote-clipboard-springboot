package com.xizeyoupan.remoteclipboard.service;

import com.xizeyoupan.remoteclipboard.entity.Clip;
import com.xizeyoupan.remoteclipboard.entity.User;

import java.util.List;

public interface UserService {

    User save(User user);

    User getByUsername(String username);

    List<Clip> getDistinctClips(User user);

    void addClip(String username, Clip clip);
}
