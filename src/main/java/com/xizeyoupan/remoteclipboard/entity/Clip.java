package com.xizeyoupan.remoteclipboard.entity;

import com.xizeyoupan.remoteclipboard.utils.ClipMode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Clip {
    private Integer connectionId;
    private String blocked;
    private String username;
    private Date modifyTime;
    private String contentType;
    private ClipMode clipMode;
    private String fileName;
    private String uuid;
}
