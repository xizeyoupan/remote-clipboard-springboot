package com.xizeyoupan.remoteclipboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Clip {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date createTime;
    private Date modifyTime;
    private String contentType;
    private Integer status; //1:OK, 2:blocked
    private String fileName;
    private String uuid;
    private String ossName;
    private String fileNameInRemote;
    private Integer userId;
    private Integer size;

}
