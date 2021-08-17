package com.xizeyoupan.remoteclipboard.service;

import com.xizeyoupan.remoteclipboard.entity.Clip;

public interface FileService {
    void put(Clip clip, byte[] bytes);

    void del(Clip clip);

    byte[] get(Clip clip);
}
