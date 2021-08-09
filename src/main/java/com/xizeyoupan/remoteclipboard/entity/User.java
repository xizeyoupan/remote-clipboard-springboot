package com.xizeyoupan.remoteclipboard.entity;

import lombok.Data;

@Data
public class User {
    private String userName;
    private String hashPassword;
    private int version;
    private int tot;
    private String token;
}
