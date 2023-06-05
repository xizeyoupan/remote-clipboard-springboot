package com.xizeyoupan.remoteclipboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xizeyoupan.remoteclipboard.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface AdapterService extends IService<File> {
    String adapterName = "";

    File getFileInfo(String path, String username);

    FileInputStream getFileInputStream(File file) throws FileNotFoundException;

    File upload(MultipartFile multipartFile, String username, String path) throws IOException;

    File newfolder(String path, String name, String username);

    List<File> index(String path, String username);

    List<File> search(String path, String username, String filter);

    File newfile(String path, String name, String username) throws IOException;

    File rename(String item, String name, String username);

    boolean delete(String path, String type, String username) throws IOException;

    boolean move(String dest, String path, String type, String username);
}
