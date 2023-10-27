package com.xizeyoupan.remoteclipboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xizeyoupan.remoteclipboard.entity.File;
import com.xizeyoupan.remoteclipboard.entity.User;

import java.util.List;

public interface FileDBService extends IService<File> {
    File getFileInfo(String path, String username);

    File newfolder(String path, String name, String username, String adapterName);

    List<File> index(String path, String username);

    List<File> search(String path, String username, String filter);

    File rename(String item, String name, String username);

    boolean move(String dest, String path, String type, String username);

    File setFile(String path, String name, User user, String uuid, File file, Long size, String adapterName);
}
