package com.xizeyoupan.remoteclipboard.service;

import com.xizeyoupan.remoteclipboard.entity.Clip;

import java.util.List;

public interface ClipService {
    Clip add(Clip clip);

    List<Clip> getList(String username);

    Clip delete(String uuid);

    Integer deleteAll(String username);

    Clip getByUuid(String uuid);

    Clip update(Clip clip);
}
