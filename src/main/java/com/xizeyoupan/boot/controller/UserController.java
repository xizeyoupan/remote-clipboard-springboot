package com.xizeyoupan.boot.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

class DataInfo {
    private ArrayList<Object> data;
    private int code;
    private String msg;

    public ArrayList<Object> getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(ArrayList<Object> data) {
        this.data = data;
    }

}

@RestController
public class UserController {

    @PostMapping("/user")
    public Object creatOrGetUser(HttpServletResponse response, @RequestParam(value = "username") String username,
                                 @RequestParam(value = "password") String password) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        DataInfo dataInfo = new DataInfo();
        if (!username.equals("sxl") || !password.equals("ILoveHorse")) {
            dataInfo.setCode(-1);
            dataInfo.setMsg("呃呃，呃呃呃，呃呃呃呃😁");
        } else {
            dataInfo.setCode(200);
            dataInfo.setMsg("太牜了");
            dataInfo.setData(new ArrayList<>());
        }

        return dataInfo;
    }
}
