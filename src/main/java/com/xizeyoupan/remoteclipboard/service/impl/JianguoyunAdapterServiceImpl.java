package com.xizeyoupan.remoteclipboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.xizeyoupan.remoteclipboard.entity.File;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.mapper.FileMapper;
import com.xizeyoupan.remoteclipboard.service.AdapterService;
import com.xizeyoupan.remoteclipboard.service.FileDBService;
import com.xizeyoupan.remoteclipboard.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class JianguoyunAdapterServiceImpl extends ServiceImpl<FileMapper, File> implements AdapterService {
    private final String adapterName = "jianguoyun";
    private final FileMapper fileMapper;
    private final UserService userService;
    private final FileDBService fileDBService;
    private Sardine sardine;

    @Value("${app.adapter.jianguoyun-adapter.file-path}")
    private String savePath;

    @Value("${app.adapter.jianguoyun-adapter.username}")
    private String username;

    @Value("${app.adapter.jianguoyun-adapter.password}")
    private String password;

    @Value("${app.adapter.jianguoyun-adapter.url}")
    private String url;

    public JianguoyunAdapterServiceImpl(FileMapper fileMapper, UserService userService, FileDBService fileDBService) {
        this.fileMapper = fileMapper;
        this.userService = userService;
        this.fileDBService = fileDBService;
    }

    @PostConstruct
    public void setSardine() {
        this.sardine = SardineFactory.begin(username, password);
        this.sardine.enablePreemptiveAuthentication(url);
    }

    @Override
    public InputStream getInputStream(File file) throws IOException {
        return sardine.get(url + savePath + file.getUuid());
    }

    @Override
    public File upload(MultipartFile multipartFile, String username, String path) throws IOException {
        User user = userService.getByUsername(username);
        String filename = multipartFile.getOriginalFilename();

        // except path ends-with "blob", path should not include filename.
        if (Objects.equals(filename, "blob")) {
            String[] strings = path.split("/");
            filename = strings[strings.length - 1];
            path = path.substring(0, path.indexOf(filename));
        }

        File file = fileDBService.getFileInfo(path + filename, username);
        String uuid;
        boolean exists = false;

        if (ObjectUtils.isEmpty(file)) {
            uuid = UUID.randomUUID().toString();
            file = new File();
            fileDBService.setFile(path, filename, user, uuid, file, multipartFile.getSize(), adapterName);
        } else {
            exists = true;
            file.setLastModified(new Date().getTime() / 1000);
            file.setFileSize(multipartFile.getSize());
            uuid = file.getUuid();
        }

        InputStream inputStream = multipartFile.getInputStream();
        String name = savePath + uuid;
        log.info("File save to jiangouyun on: " + name);
        log.warn(url + name);

        sardine.exists(url + name);
        sardine.put(url + name, inputStream);

        if (exists) fileMapper.updateById(file);
        else fileMapper.insert(file);

        return file;
    }

    @Override
    public File newfile(String path, String name, String username) throws IOException {
        User user = userService.getByUsername(username);
        String uuid = UUID.randomUUID().toString();
        File file = new File();
        fileDBService.setFile(path, name, user, uuid, file, 0L, adapterName);

        String fileName = savePath + uuid;
        log.info("File save to jiangouyun on: " + fileName);

        log.warn(url + fileName);

        sardine.exists(url + fileName);
        sardine.put(url + fileName, InputStream.nullInputStream());


        fileMapper.insert(file);
        return file;
    }

    @Override
    public boolean delete(String path, String type, String username) throws IOException {
        if (Objects.equals(type, "file")) {
            File file = fileDBService.getFileInfo(path, username);
            fileMapper.deleteById(file);

            sardine.delete(url + savePath + file.getUuid());
            return true;

        } else if (Objects.equals(type, "dir")) {
            File dir = fileDBService.getFileInfo(path, username);
            fileMapper.deleteById(dir);

            User user = userService.getByUsername(username);
            if (!path.endsWith("/")) path += "/";
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", user.getId());
            queryWrapper.like("path", path + "%");
            List<File> files = fileMapper.selectList(queryWrapper);
            remove(queryWrapper);

            for (File file : files) {
                if (file.getType().equals("dir")) continue;
                String remotePath = savePath + file.getUuid();
                log.info("Delete file: " + remotePath);
                sardine.delete(url + remotePath);
            }
            return true;
        }
        return false;
    }
}
