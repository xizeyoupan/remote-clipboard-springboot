package com.xizeyoupan.boot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Clip {
    private long username;
    private long builtTime;
    private long changeTime;
    private String contentType;
    private String content;
}
