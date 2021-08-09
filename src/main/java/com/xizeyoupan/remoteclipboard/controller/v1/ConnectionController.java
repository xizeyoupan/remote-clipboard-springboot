package com.xizeyoupan.remoteclipboard.controller.v1;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class ConnectionController {

    @GetMapping("/connections")
    public String login(){
        return "eeeeeeee断电";
    }

}
