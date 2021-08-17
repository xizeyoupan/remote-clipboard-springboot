package com.xizeyoupan.remoteclipboard.controller.v1;

import com.xizeyoupan.remoteclipboard.entity.Clip;
import com.xizeyoupan.remoteclipboard.service.FileService;
import com.xizeyoupan.remoteclipboard.service.SseService;
import com.xizeyoupan.remoteclipboard.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;

@RestController
@Slf4j
@RequestMapping("api/v1/file")
public class FileController {
    final UserService userService;
    final SseService sseService;
    final FileService fileService;

    public FileController(UserService userService, SseService sseService, FileService fileService) {
        this.userService = userService;
        this.sseService = sseService;
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestPart("clip") Clip clip, @RequestPart(value = "file") MultipartFile file) throws IOException {
        System.out.println("clip = " + clip);
        System.out.println("file = " + file);
        userService.addClip(clip.getUsername(), clip);
        fileService.put(clip, file.getBytes());
        sseService.sendMsgForUser(clip.getUsername(), Collections.singletonList(clip));
    }

    @PostMapping(value = "/download")
    public void download(@RequestBody Clip clip, HttpServletResponse response) throws Exception {
        System.out.println("clip = " + clip);
        byte[] bytes = fileService.get(clip);

        InputStream is = new ByteArrayInputStream(bytes);
        if (ObjectUtils.isEmpty(is)) {
            log.error("file not found");
            return;
        }
        ServletOutputStream os = response.getOutputStream();
        response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(clip.getFileName(), "UTF-8"));
        IOUtils.copy(is, os);
        IOUtils.closeQuietly(is);
        IOUtils.closeQuietly(os);
    }

}