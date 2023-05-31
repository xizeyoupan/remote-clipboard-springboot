package com.xizeyoupan.remoteclipboard.adapter;

import com.xizeyoupan.remoteclipboard.entity.File;
import com.xizeyoupan.remoteclipboard.entity.User;

import java.io.InputStream;

public interface BaseAdapter {
    File[] getFiles(User user);

    File uploadFile(InputStream inputStream);

    Integer getCapacity();

    Integer getUsedCapacity();

}
