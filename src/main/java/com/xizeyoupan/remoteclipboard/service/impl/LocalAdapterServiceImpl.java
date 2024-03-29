package com.xizeyoupan.remoteclipboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class LocalAdapterServiceImpl extends ServiceImpl<FileMapper, File> implements AdapterService {
    private final String adapterName = "local";
    private final FileMapper fileMapper;
    private final UserService userService;
    private final FileDBService fileDBService;

    @Value("${app.adapter.local-adapter.file-path}")
    private String localSavePath;

    public LocalAdapterServiceImpl(FileMapper fileMapper, UserService userService, FileDBService fileDBService) {
        this.fileMapper = fileMapper;
        this.userService = userService;
        this.fileDBService = fileDBService;
    }

    @PostConstruct
    void mkdir() throws IOException {
        log.info("File path: " + localSavePath);
        Path path = Paths.get(localSavePath);
        Files.createDirectories(path);
    }


    @Override
    public InputStream getInputStream(File file) throws FileNotFoundException {
        String s = localSavePath + file.getUuid();
        return new FileInputStream(s);
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
        String name = localSavePath + uuid;
        log.info("File save locally to: " + localSavePath);
        FileOutputStream fileOutputStream = new FileOutputStream(name);
        StreamUtils.copy(inputStream, fileOutputStream);
        inputStream.close();
        fileOutputStream.close();

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

        String fileName = localSavePath + uuid;
        log.info("File save to: " + localSavePath);
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.close();

        fileMapper.insert(file);
        return file;
    }


    @Override
    public boolean delete(String path, String type, String username) throws IOException {
        if (Objects.equals(type, "file")) {
            File file = fileDBService.getFileInfo(path, username);
            fileMapper.deleteById(file);
            Path localPath = Paths.get(localSavePath + file.getUuid());
            Files.delete(localPath);
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
                Path localPath = Paths.get(localSavePath + file.getUuid());
                log.info("Delete local file: " + localPath);
                Files.delete(localPath);
            }
            return true;
        }
        return false;
    }

}
