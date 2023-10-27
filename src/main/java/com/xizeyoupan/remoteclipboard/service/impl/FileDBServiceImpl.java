package com.xizeyoupan.remoteclipboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xizeyoupan.remoteclipboard.entity.File;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.mapper.FileMapper;
import com.xizeyoupan.remoteclipboard.service.FileDBService;
import com.xizeyoupan.remoteclipboard.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FileDBServiceImpl extends ServiceImpl<FileMapper, File> implements FileDBService {

    private final FileMapper fileMapper;
    private final UserService userService;

    public FileDBServiceImpl(FileMapper fileMapper, UserService userService) {
        this.fileMapper = fileMapper;
        this.userService = userService;
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
    public File newfolder(String path, String name, String username, String adapterName) {
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

    @Override
    public File setFile(String path, String name, User user, String uuid, File file, Long size,String adapterName) {
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

        return file;
    }
}
