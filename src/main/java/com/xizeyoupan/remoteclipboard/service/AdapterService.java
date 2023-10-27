package com.xizeyoupan.remoteclipboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xizeyoupan.remoteclipboard.entity.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface AdapterService extends IService<File> {
    String adapterName = "";

    InputStream getInputStream(File file) throws IOException;

    File upload(MultipartFile multipartFile, String username, String path) throws IOException;


    File newfile(String path, String name, String username) throws IOException;


    boolean delete(String path, String type, String username) throws IOException;

}
