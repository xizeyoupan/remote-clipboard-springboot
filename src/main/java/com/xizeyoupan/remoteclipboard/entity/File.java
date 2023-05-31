package com.xizeyoupan.remoteclipboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class File {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @JsonProperty("create_time")
    private Long createTime;
    @JsonProperty("last_modified")
    private Long lastModified;
    private String uuid;
    @JsonProperty("mime_type")
    private String mimeType;
    private String path;
    private String storage;
    private String type;
    private String visibility;
    private String basename;
    private String extension;
    private Integer userId;
    @JsonProperty("file_size")
    private Long fileSize;

}
