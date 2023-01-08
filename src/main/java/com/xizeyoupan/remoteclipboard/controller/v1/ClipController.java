package com.xizeyoupan.remoteclipboard.controller.v1;

import com.xizeyoupan.remoteclipboard.entity.Clip;
import com.xizeyoupan.remoteclipboard.entity.User;
import com.xizeyoupan.remoteclipboard.oss.UcloudConfig;
import com.xizeyoupan.remoteclipboard.service.ClipService;
import com.xizeyoupan.remoteclipboard.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/clip")
public class ClipController {

    private ClipService clipService;
    private UserService userService;
    private UcloudConfig ucloudConfig;

    public ClipController(ClipService clipService, UserService userService, UcloudConfig ucloudConfig) {
        this.clipService = clipService;
        this.userService = userService;
        this.ucloudConfig = ucloudConfig;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getList(HttpSession session) {
        String username = session.getAttribute("username").toString();
        List<Clip> list = clipService.getList(username);
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "Success.");
        map.put("data", list);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@RequestBody Clip clip, HttpSession session) {
        String username = session.getAttribute("username").toString();
        Map<String, Object> map = new HashMap<>();
        User user = userService.getByUsername(username);
        clip.setOssName(ucloudConfig.getName());
        clip.setFileNameInRemote(clip.getUuid());
        clip.setUserId(user.getId());
        clipService.add(clip);
        map.put("msg", "Success.");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Clip clip, HttpSession session) {
        String username = session.getAttribute("username").toString();
        Map<String, Object> map = new HashMap<>();
        User user = userService.getByUsername(username);
        Clip remote = clipService.getByUuid(clip.getUuid());
        if (user.getId().equals(remote.getUserId())) {
            clipService.delete(remote.getUuid());
            map.put("msg", "Success.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("msg", "Incorrect data.");
            return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/delete/all")
    public ResponseEntity<Map<String, Object>> deleteAll(HttpSession session) {
        String username = session.getAttribute("username").toString();
        Map<String, Object> map = new HashMap<>();
        clipService.deleteAll(username);
        map.put("msg", "Success.");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody Clip clip, HttpSession session) {
        String username = session.getAttribute("username").toString();
        Map<String, Object> map = new HashMap<>();
        User user = userService.getByUsername(username);
        Clip remote = clipService.getByUuid(clip.getUuid());
        if (user.getId().equals(remote.getUserId())) {
            remote.setModifyTime(new Date());
            remote.setStatus(clip.getStatus());
            clipService.update(remote);
            map.put("msg", "Success.");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("msg", "Incorrect data.");
            return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
        }
    }
}
