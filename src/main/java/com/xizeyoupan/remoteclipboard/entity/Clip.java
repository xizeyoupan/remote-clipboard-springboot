package com.xizeyoupan.remoteclipboard.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Clip {
    private String userName;
    private Date builtTime;
    private Date changeTime;
    private String contentType;
}
