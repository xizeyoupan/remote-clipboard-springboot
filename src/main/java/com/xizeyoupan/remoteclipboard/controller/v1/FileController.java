package com.xizeyoupan.remoteclipboard.controller.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xizeyoupan.remoteclipboard.entity.File;
import com.xizeyoupan.remoteclipboard.entity.Index;
import com.xizeyoupan.remoteclipboard.service.AdapterService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class FileController {
    private final ApplicationContext applicationContext;
    private AdapterService adapterService;
    private List<String> storages;

    public FileController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public AdapterService getAdapterService(String adapter) throws ClassNotFoundException {
        String adapterName = "com.xizeyoupan.remoteclipboard.service.impl." + adapter.substring(0, 1).toUpperCase() + adapter.substring(1) + "AdapterServiceImpl";
//        log.info(adapterName);
        Class<?> adapterClass = Class.forName(adapterName);
        return (AdapterService) applicationContext.getBean(adapterClass);
    }

    @GetMapping
    public ResponseEntity<Object> handle(HttpSession session, String q, String adapter, HttpServletRequest request) throws ClassNotFoundException, IOException {
        String username = session.getAttribute("username").toString();

        adapterService = getAdapterService(adapter);

        switch (q) {
            case "index" -> {
                String path = request.getParameter("path");
                if (ObjectUtils.isEmpty(path)) {
                    path = adapter + "://";
                }
                if (!path.endsWith("/")) path += '/';

                List<File> files = adapterService.index(path, username);
                Index index = getIndex(adapter, storages, path, files);
                return ResponseEntity.ok(index);
            }

            case "download" -> {
                String path = request.getParameter("path");
                File file = adapterService.getFileInfo(path, username);
                FileInputStream fileInputStream = adapterService.getFileInputStream(file);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getBasename(), StandardCharsets.UTF_8));

                String mimeType = file.getMimeType();
                if (ObjectUtils.isEmpty(mimeType)) {
                    mimeType = "application/octet-stream";
                }

                return ResponseEntity.ok().headers(headers).contentLength(file.getFileSize()).contentType(MediaType.parseMediaType(mimeType)).body(new InputStreamResource(fileInputStream));
            }

            case "newfolder" -> {
                String name = request.getParameter("name");
                String path = request.getParameter("path");
                if (!path.endsWith("/")) path += '/';

                adapterService.newfolder(path, name, username);
                List<File> files = adapterService.index(path, username);
                Index index = getIndex(adapter, storages, path, files);
                return ResponseEntity.ok(index);
            }

            case "newfile" -> {
                String name = request.getParameter("name");
                String path = request.getParameter("path");
                if (!path.endsWith("/")) path += '/';

                adapterService.newfile(path, name, username);
                List<File> files = adapterService.index(path, username);
                Index index = getIndex(adapter, storages, path, files);
                return ResponseEntity.ok(index);
            }

            case "preview" -> {
                String path = request.getParameter("path");
                File file = adapterService.getFileInfo(path, username);
                FileInputStream fileInputStream = adapterService.getFileInputStream(file);
                String mimeType = file.getMimeType();
                if (ObjectUtils.isEmpty(mimeType)) {
                    mimeType = "application/octet-stream";
                }
                return ResponseEntity.ok().contentLength(file.getFileSize()).contentType(MediaType.parseMediaType(mimeType)).body(new InputStreamResource(fileInputStream));

            }
            case "search" -> {
                String filter = request.getParameter("filter");
                String path = request.getParameter("path");
                if (!path.endsWith("/")) path += '/';

                List<File> files = adapterService.search(path, username, filter);
                Index index = getIndex(adapter, storages, path, files);
                return ResponseEntity.ok(index);
            }
            case "rename" -> {
                String name = request.getParameter("name");
                String item = request.getParameter("item");
                String path = request.getParameter("path");
                if (!path.endsWith("/")) path += '/';

                adapterService.rename(item, name, username);
                List<File> files = adapterService.index(path, username);
                Index index = getIndex(adapter, storages, path, files);
                return ResponseEntity.ok(index);
            }
            case "delete" -> {
                String items = request.getParameter("items");
                String pathParameter = request.getParameter("path");
                ObjectMapper objectMapper = new ObjectMapper();

                List<Map<String, String>> list = objectMapper.readValue(items, new TypeReference<>() {
                });
                for (Map<String, String> stringStringMap : list) {
                    String path = stringStringMap.get("path");
                    String type = stringStringMap.get("type");
                    adapterService.delete(path, type, username);
                }

                if (!pathParameter.endsWith("/")) pathParameter += '/';
                List<File> files = adapterService.index(pathParameter, username);
                Index index = getIndex(adapter, storages, pathParameter, files);
                return ResponseEntity.ok(index);
            }
            case "move" -> {
                String path = request.getParameter("path");
                if (ObjectUtils.isEmpty(path)) {
                    path = adapter + "://";
                }
                if (!path.endsWith("/")) path += '/';
                String items = request.getParameter("items");
                String dest = request.getParameter("item");

                ObjectMapper objectMapper = new ObjectMapper();

                List<Map<String, String>> list = objectMapper.readValue(items, new TypeReference<>() {
                });
                for (Map<String, String> stringStringMap : list) {
                    String filePath = stringStringMap.get("path");
                    String type = stringStringMap.get("type");
                    adapterService.move(dest, filePath, type, username);
                }

                List<File> files = adapterService.index(path, username);
                Index index = getIndex(adapter, storages, path, files);
                return ResponseEntity.ok(index);

            }
            default -> {
            }
        }
        return null;
    }

    private Index getIndex(String adapter, List<String> storages, String path, List<File> list) {
        Index index = new Index();
        index.setFiles(list);
        index.setAdapter(adapter);
        index.setStorages(storages);
        if (!path.endsWith("://") && path.endsWith("/")) path = path.substring(0, path.length() - 1);
        index.setDirname(path);
        return index;
    }

    @PostMapping
    public ResponseEntity<Object> handle(HttpSession session, String q, String adapter, String path, MultipartHttpServletRequest request) throws ClassNotFoundException, IOException {
        String username = session.getAttribute("username").toString();
        adapterService = getAdapterService(adapter);

        switch (q) {
            case "upload" -> {
                if (!path.endsWith("/")) path += '/';
                String finalPath = path;
                request.getMultiFileMap().forEach((k, v) -> {
                    for (MultipartFile multipartFile : v) {
                        try {
                            adapterService.upload(multipartFile, username, finalPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                return new ResponseEntity<>(HttpStatus.OK);
            }
            case "save" -> {
                String[] strings = path.split("/");
                String filename = strings[strings.length - 1];
                path = path.substring(0, path.indexOf(filename));
                String content = request.getParameter("content");
                byte[] bytes = content.getBytes();
                MockMultipartFile mockMultipartFile = new MockMultipartFile(filename, filename, null, bytes);
                adapterService.upload(mockMultipartFile, username, path);
                return ResponseEntity.ok(content);
            }
            default -> {
            }
        }

        return null;
    }

    @PostConstruct
    private List<String> getStorages() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:com/xizeyoupan/remoteclipboard/service/impl/*.class");

        List<String> storages = new ArrayList<>();
        for (Resource resource : resources) {
            String clsName = new SimpleMetadataReaderFactory().getMetadataReader(resource).getClassMetadata().getClassName();
            String[] strings = clsName.split("\\.");
            String pkgName = strings[strings.length - 1];
            if (pkgName.contains("AdapterServiceImpl")) {
                pkgName = pkgName.substring(0, pkgName.indexOf("AdapterServiceImpl"));
                storages.add(pkgName.toLowerCase());
            }
        }
        this.storages = storages;
        return storages;
    }

}
