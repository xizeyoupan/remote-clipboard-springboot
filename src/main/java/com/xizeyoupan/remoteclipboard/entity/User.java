package com.xizeyoupan.remoteclipboard.entity;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private String username;
    private String password;
    private List<Clip> timeline;
    private Integer tot;
}
