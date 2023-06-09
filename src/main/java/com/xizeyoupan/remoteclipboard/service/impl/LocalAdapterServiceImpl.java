package com.xizeyoupan.remoteclipboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xizeyoupan.remoteclipboard.mapper.FileMapper;
import com.xizeyoupan.remoteclipboard.entity.File;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.service.AdapterService;
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
import java.text.MessageFormat;
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
    @Value("${app.adapter.local-adapter.file-path}")
    private String localSavePath;

    public LocalAdapterServiceImpl(FileMapper fileMapper, UserService userService) {
        this.fileMapper = fileMapper;
        this.userService = userService;
    }

    @PostConstruct
    void mkdir() throws IOException {
        log.info("File path: " + localSavePath);
        Path path = Paths.get(localSavePath);
        Files.createDirectories(path);
    }

    @Override
    public File getFileInfo(String path, String username) {
        User user = userService.getByUsername(username);
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("path", path);
        queryWrapper.eq("user_id", user.getId());
        return fileMapper.selectOne(queryWrapper);
    }

    @Override
    public FileInputStream getFileInputStream(File file) throws FileNotFoundException {
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

        File file = getFileInfo(path + filename, username);
        String uuid;
        boolean exists = false;

        if (ObjectUtils.isEmpty(file)) {
            uuid = UUID.randomUUID().toString();
            file = new File();
            setFile(path, filename, user, uuid, file, multipartFile.getSize());
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
    public File newfolder(String path, String name, String username) {
        User user = userService.getByUsername(username);
        File file = new File();

        file.setStorage(adapterName);
        file.setBasename(name);
        file.setCreateTime(new Date().getTime() / 1000);
        file.setLastModified(file.getCreateTime());
        file.setPath(path + name);
        file.setUserId(user.getId());
        file.setType("dir");
        file.setVisibility("public");

        fileMapper.insert(file);
        return file;

    }

    @Override
    public List<File> index(String path, String username) {
        User user = userService.getByUsername(username);
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.like("path", path + "%");
        queryWrapper.notLike("path", path + "%/%");
        return fileMapper.selectList(queryWrapper);
    }

    @Override
    public List<File> search(String path, String username, String filter) {
        User user = userService.getByUsername(username);
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.like("path", path + "%" + filter + "%");
        queryWrapper.notLike("path", path + "%" + filter + "%/%");
        return fileMapper.selectList(queryWrapper);
    }

    @Override
    public File newfile(String path, String name, String username) throws IOException {
        User user = userService.getByUsername(username);
        String uuid = UUID.randomUUID().toString();
        File file = new File();
        setFile(path, name, user, uuid, file, 0L);

        String fileName = localSavePath + uuid;
        log.info("File save to: " + localSavePath);
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.close();

        fileMapper.insert(file);
        return file;
    }

    @Override
    public File rename(String item, String name, String username) {
        File file = getFileInfo(item, username);
        assert !ObjectUtils.isEmpty(file);
        String filePath = file.getPath();
        String[] strings = filePath.split("/");
        String basename = strings[strings.length - 1];

        filePath = filePath.substring(0, filePath.indexOf(basename)) + name;
        file.setBasename(name);
        file.setMimeType(URLConnection.guessContentTypeFromName(name));
        file.setLastModified(new Date().getTime() / 1000);
        file.setPath(filePath);
        fileMapper.updateById(file);

        if (Objects.equals(file.getType(), "dir")) {
            User user = userService.getByUsername(username);
            UpdateWrapper<File> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", user.getId());
            updateWrapper.like("path", item + "/%");
            String format = MessageFormat.format("`path` = REPLACE(`path`, ''{0}'', ''{1}'')", item, filePath);
            updateWrapper.setSql(format);
            update(updateWrapper);
        }
        return file;
    }

    @Override
    public boolean delete(String path, String type, String username) throws IOException {
        if (Objects.equals(type, "file")) {
            File file = getFileInfo(path, username);
            fileMapper.deleteById(file);
            Path localPath = Paths.get(localSavePath + file.getUuid());
            Files.delete(localPath);
            return true;
        } else if (Objects.equals(type, "dir")) {
            File dir = getFileInfo(path, username);
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

    @Override
    public boolean move(String dest, String path, String type, String username) {
        if (Objects.equals(type, "file")) {
            File file = getFileInfo(path, username);
            file.setPath(dest + '/' + file.getBasename());
            fileMapper.updateById(file);
            return true;
        } else if (Objects.equals(type, "dir")) {
            File dir = getFileInfo(path, username);
            String[] strings = path.split("/");
            String dirname = strings[strings.length - 1];

            dir.setPath(dest + '/' + dirname);
            fileMapper.updateById(dir);

            User user = userService.getByUsername(username);
            if (!path.endsWith("/")) path += "/";
            UpdateWrapper<File> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", user.getId());
            updateWrapper.like("path", path + "%");
            String newPath = dest + '/' + dir.getBasename() + '/';
            String pattern = "`path` = REPLACE(`path`, ''{0}'', ''{1}'')";
            String format = MessageFormat.format(pattern, path, newPath);
            updateWrapper.setSql(format);
            update(updateWrapper);
            return true;
        }
        return false;
    }

    private void setFile(String path, String name, User user, String uuid, File file, Long size) {
        file.setUuid(uuid);
        file.setStorage(adapterName);
        file.setBasename(name);
        assert name != null;
        String[] strings = name.split("\\.");
        if (strings.length > 1) {
            file.setExtension(strings[strings.length - 1]);
        }

        file.setFileSize(size);
        file.setCreateTime(new Date().getTime() / 1000);
        file.setLastModified(file.getCreateTime());
        file.setMimeType(URLConnection.guessContentTypeFromName(name));
        file.setPath(path + name);
        file.setUserId(user.getId());
        file.setType("file");
        file.setVisibility("public");
    }

}
